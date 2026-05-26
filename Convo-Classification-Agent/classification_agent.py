"""
Risk Classification Agent
Two approaches for labeling therapy transcripts as HIGH_RISK or NOT_HIGH_RISK:
  - Approach 1: Single chain-of-thought classifier
  - Approach 2: Multi-analyst ensemble
"""

import json
import re
import time
from together import Together
from prompts import (
    TRIAGE_PROMPT,
    SINGLE_COT_CLASSIFIER_PROMPT,
    LINGUISTIC_ANALYST_PROMPT,
    EMOTIONAL_ANALYST_PROMPT,
    CONTEXTUAL_ANALYST_PROMPT,
    BEHAVIORAL_ANALYST_PROMPT,
    RISK_AGGREGATOR_PROMPT,
    META_JUDGE_PROMPT,
)

client = Together(api_key="tgp_v1_Y14MDW50XKA3rLhWcs-sbNKFFnpY9g6z99xQKc1V-uI")

# ========== CONFIGURATION ==========

MAX_CTX = 4096
CLASSIFIER_MAX_TOKENS = 1024
ANALYST_MAX_TOKENS = 768
AGGREGATOR_MAX_TOKENS = 1024
JUDGE_MAX_TOKENS = 768

DEFAULT_MODEL = "meta-llama/Llama-3.3-70B-Instruct-Turbo"

ENSEMBLE_MODELS = {
    "DeepSeek": "deepseek-ai/DeepSeek-V3-0324",
    "Llama3": "meta-llama/Llama-3.3-70B-Instruct-Turbo",
    "Qwen": "Qwen/Qwen3.5-397B-A17B",
}

ANALYST_CONFIGS = {
    "linguistic": LINGUISTIC_ANALYST_PROMPT,
    "emotional": EMOTIONAL_ANALYST_PROMPT,
    "contextual": CONTEXTUAL_ANALYST_PROMPT,
    "behavioral": BEHAVIORAL_ANALYST_PROMPT,
}

# ========== API WRAPPER ==========


def call_together(
    model_id: str,
    messages: list[dict],
    *,
    max_tokens: int,
    temperature: float = 0.3,
    stop: list[str] | None = None,
    max_retries: int = 5,
) -> str:
    """Call Together AI chat completions API (v2 SDK) with retry on 503."""
    import time as _time

    kwargs = dict(
        model=model_id,
        messages=messages,
        max_tokens=max_tokens,
        temperature=temperature,
    )
    if stop:
        kwargs["stop"] = stop

    for attempt in range(max_retries):
        try:
            response = client.chat.completions.create(**kwargs)
            return response.choices[0].message.content.strip()
        except Exception as e:
            if "503" in str(e) or "service_unavailable" in str(e).lower():
                wait = 2 ** attempt * 5
                print(f"    [503 - retrying in {wait}s (attempt {attempt + 1}/{max_retries})]")
                _time.sleep(wait)
            else:
                raise
    raise RuntimeError(f"API unavailable after {max_retries} retries")


def parse_json_response(raw: str) -> dict:
    """Extract JSON from a model response, handling code fences and formatting issues."""
    text = re.sub(r'```(?:json)?\s*', '', raw)
    text = text.replace('```', '')
    json_match = re.search(r'\{[\s\S]*\}', text)
    if not json_match:
        return {}
    json_str = json_match.group(0)
    try:
        return json.loads(json_str)
    except json.JSONDecodeError:
        json_str = re.sub(r',\s*}', '}', json_str)
        json_str = re.sub(r',\s*]', ']', json_str)
        try:
            return json.loads(json_str)
        except json.JSONDecodeError:
            return {}


# ========== APPROACH 1: SINGLE CHAIN-OF-THOUGHT CLASSIFIER ==========


def classify_single_cot(
    transcript: str,
    model_id: str = DEFAULT_MODEL,
    temperature: float = 0.2,
) -> dict:
    """
    Classify a transcript using a single chain-of-thought prompt.
    Returns a dict with label, confidence, reasoning, and subcategory breakdown.
    """
    prompt_filled = SINGLE_COT_CLASSIFIER_PROMPT.replace("{transcript}", transcript)
    messages = [
        {"role": "system", "content": "You are a clinical risk assessment AI. Output only valid JSON."},
        {"role": "user", "content": prompt_filled},
    ]

    raw = call_together(
        model_id,
        messages,
        max_tokens=CLASSIFIER_MAX_TOKENS,
        temperature=temperature,
    )

    result = parse_json_response(raw)

    if not result:
        return {
            "label": "NOT_HIGH_RISK",
            "confidence": 0.0,
            "reasoning": "Failed to parse model output",
            "risk_subcategories": {
                "self_harm": "none",
                "crisis": "none",
                "violence": "none",
                "abuse": "none",
            },
            "raw_output": raw,
            "parse_error": True,
        }

    label = result.get("label", "NOT_HIGH_RISK").upper()
    if "HIGH" in label and "NOT" not in label:
        label = "HIGH_RISK"
    else:
        label = "NOT_HIGH_RISK"
    result["label"] = label

    confidence = result.get("confidence", 0.5)
    if isinstance(confidence, str):
        try:
            confidence = float(confidence)
        except ValueError:
            confidence = 0.5
    result["confidence"] = max(0.0, min(1.0, confidence))

    return result


# ========== APPROACH 2: MULTI-ANALYST ENSEMBLE ==========


def run_analyst(
    analyst_name: str,
    transcript: str,
    model_id: str = DEFAULT_MODEL,
    temperature: float = 0.1,
) -> dict:
    """Run a single analyst on the transcript."""
    prompt_template = ANALYST_CONFIGS[analyst_name]
    prompt_filled = prompt_template.replace("{transcript}", transcript)
    messages = [
        {"role": "system", "content": f"You are the {analyst_name} analyst. Output only valid JSON."},
        {"role": "user", "content": prompt_filled},
    ]

    raw = call_together(
        model_id,
        messages,
        max_tokens=ANALYST_MAX_TOKENS,
        temperature=temperature,
    )

    result = parse_json_response(raw)
    result["_analyst"] = analyst_name
    result["_raw"] = raw
    return result


RATING_ORDER = ["NOT_PRESENT", "LOW", "MEDIUM", "HIGH"]


def _majority_rating(ratings: list[str]) -> str:
    """Return the most common rating from a list. Ties break toward the higher risk."""
    from collections import Counter
    counts = Counter(r.upper() for r in ratings if r)
    if not counts:
        return "LOW"
    max_count = max(counts.values())
    tied = [r for r, c in counts.items() if c == max_count]
    if len(tied) == 1:
        return tied[0]
    for level in reversed(RATING_ORDER):
        if level in tied:
            return level
    return tied[0]


def _merge_analyst_runs(runs: list[dict], analyst_name: str) -> dict:
    """Merge multiple runs of the same analyst into a consensus result."""
    if len(runs) == 1:
        return runs[0]

    merged = {}
    all_keys = set()
    for run in runs:
        all_keys.update(k for k in run if not k.startswith("_"))

    for key in all_keys:
        values = [run.get(key) for run in runs if key in run]
        if not values:
            continue

        if isinstance(values[0], dict) and "level" in values[0]:
            levels = [v.get("level", "LOW") for v in values if isinstance(v, dict)]
            merged_field = {"level": _majority_rating(levels)}
            all_evidence = []
            for v in values:
                if isinstance(v, dict):
                    all_evidence.extend(v.get("evidence", []))
            seen = set()
            unique_evidence = []
            for e in all_evidence:
                if e not in seen:
                    seen.add(e)
                    unique_evidence.append(e)
            merged_field["evidence"] = unique_evidence[:5]
            for v in values:
                if isinstance(v, dict):
                    for subkey in v:
                        if subkey not in ("level", "evidence") and subkey not in merged_field:
                            sub_vals = [x.get(subkey) for x in values if isinstance(x, dict) and subkey in x]
                            if sub_vals and isinstance(sub_vals[0], str):
                                merged_field[subkey] = _majority_rating(sub_vals) if all(s in RATING_ORDER for s in [sv.upper() for sv in sub_vals if sv]) else max(set(sub_vals), key=sub_vals.count)
                            else:
                                merged_field[subkey] = sub_vals[0]
            merged[key] = merged_field
        elif key.startswith("overall_"):
            merged[key] = _majority_rating([str(v) for v in values])
        elif key == "summary":
            merged[key] = max(values, key=len) if values else ""
        else:
            merged[key] = values[0]

    merged["_analyst"] = analyst_name
    merged["_raw"] = runs[0].get("_raw", "")
    merged["_consistency_runs"] = len(runs)
    return merged


def run_all_analysts(
    transcript: str,
    model_id: str = DEFAULT_MODEL,
    consistency_runs: int = 1,
) -> dict[str, dict]:
    """Run all four analysts on the transcript, optionally with self-consistency."""
    results = {}
    for name in ANALYST_CONFIGS:
        if consistency_runs > 1:
            runs = [run_analyst(name, transcript, model_id) for _ in range(consistency_runs)]
            results[name] = _merge_analyst_runs(runs, name)
        else:
            results[name] = run_analyst(name, transcript, model_id)
    return results


def aggregate_analyst_reports(
    analyst_results: dict[str, dict],
    model_id: str = DEFAULT_MODEL,
    explicit_flag: bool = False,
) -> dict:
    """Aggregate the four analyst reports into a single classification."""
    if explicit_flag:
        return {
            "label": "HIGH_RISK",
            "confidence": 0.97,
            "reasoning": "Triage gate detected explicit risk signal — fast path to HIGH_RISK.",
            "escalation_rule_triggered": "5",
            "convergence_matrix": {},
        }
    reports_text = ""
    for name, result in analyst_results.items():
        summary = result.get("summary", result.get("_raw", "No summary available"))
        overall = result.get(f"overall_{name}_risk", "UNKNOWN")
        reports_text += f"\n[{name.upper()} ANALYST]\nOverall risk: {overall}\nSummary: {summary}\n"
        reports_text += f"Full report: {json.dumps({k: v for k, v in result.items() if not k.startswith('_')}, indent=2)}\n"
        reports_text += "-" * 40 + "\n"

    prompt_filled = RISK_AGGREGATOR_PROMPT.replace("{analyst_reports}", reports_text)
    messages = [
        {"role": "system", "content": "You are a senior risk assessment supervisor. Output only valid JSON."},
        {"role": "user", "content": prompt_filled},
    ]

    raw = call_together(
        model_id,
        messages,
        max_tokens=AGGREGATOR_MAX_TOKENS,
        temperature=0.2,
    )

    result = parse_json_response(raw)
    if not result:
        return {
            "label": "NOT_HIGH_RISK",
            "confidence": 0.0,
            "reasoning": "Failed to parse aggregator output",
            "raw_output": raw,
            "parse_error": True,
        }

    label = result.get("label", "NOT_HIGH_RISK").upper()
    if "HIGH" in label and "NOT" not in label:
        label = "HIGH_RISK"
    else:
        label = "NOT_HIGH_RISK"
    result["label"] = label

    confidence = result.get("confidence", 0.5)
    if isinstance(confidence, str):
        try:
            confidence = float(confidence)
        except ValueError:
            confidence = 0.5
    result["confidence"] = max(0.0, min(1.0, confidence))

    return result


def meta_judge(
    transcript: str,
    classification: dict,
    model_id: str = DEFAULT_MODEL,
) -> dict:
    """Optional meta-judge that validates the classification against the transcript."""
    classification_text = json.dumps(
        {k: v for k, v in classification.items() if not k.startswith("_")},
        indent=2,
    )
    prompt_filled = META_JUDGE_PROMPT.replace(
        "{transcript}", transcript
    ).replace(
        "{classification}", classification_text
    )
    messages = [
        {"role": "system", "content": "You are a QA reviewer for risk assessments. Output only valid JSON."},
        {"role": "user", "content": prompt_filled},
    ]

    raw = call_together(
        model_id,
        messages,
        max_tokens=JUDGE_MAX_TOKENS,
        temperature=0.1,
    )

    result = parse_json_response(raw)
    return result


def run_triage(
    transcript: str,
    model_id: str = DEFAULT_MODEL,
) -> dict:
    """Run the triage gate to determine routing path."""
    prompt_filled = TRIAGE_PROMPT.replace("{transcript}", transcript)
    messages = [
        {"role": "system", "content": "You are a conversation routing classifier. Output only valid JSON."},
        {"role": "user", "content": prompt_filled},
    ]
    raw = call_together(model_id, messages, max_tokens=200, temperature=0.1)
    result = parse_json_response(raw)
    if not result:
        return {"routing": "FULL_ENSEMBLE", "rationale": "Failed to parse triage output"}
    return result


def classify_ensemble(
    transcript: str,
    model_id: str = DEFAULT_MODEL,
    use_meta_judge: bool = True,
    use_triage: bool = False,
    consistency_runs: int = 2,
) -> dict:
    """
    Full ensemble classification pipeline:
    1. Triage gate (routes to fast path or full analysis)
    2. Run all 4 analysts with self-consistency (if FULL_ENSEMBLE)
    3. Aggregate their reports with convergence matrix
    4. Validate with meta-judge
    Returns final classification dict.
    """
    triage_result = None
    if use_triage:
        triage_result = run_triage(transcript, model_id)
        routing = triage_result.get("routing", "FULL_ENSEMBLE")

        if routing == "CLEAR_NOT_HIGH_RISK":
            return {
                "label": "NOT_HIGH_RISK",
                "confidence": 0.92,
                "reasoning": "Triage gate: clear benign conversation with no risk indicators.",
                "triage": triage_result,
                "analyst_details": {},
            }

        if routing == "EXPLICIT_FAST_PATH":
            aggregated = aggregate_analyst_reports({}, model_id, explicit_flag=True)
            aggregated["triage"] = triage_result
            aggregated["analyst_details"] = {}
            return aggregated

    analyst_results = run_all_analysts(transcript, model_id, consistency_runs=consistency_runs)
    aggregated = aggregate_analyst_reports(analyst_results, model_id)

    if use_meta_judge and not aggregated.get("parse_error"):
        judge_result = meta_judge(transcript, aggregated, model_id)
        if judge_result:
            final_label = judge_result.get("final_label", aggregated["label"])
            if "HIGH" in final_label.upper() and "NOT" not in final_label.upper():
                final_label = "HIGH_RISK"
            else:
                final_label = "NOT_HIGH_RISK"
            aggregated["label"] = final_label

            adjusted_conf = judge_result.get("adjusted_confidence", aggregated["confidence"])
            if isinstance(adjusted_conf, str):
                try:
                    adjusted_conf = float(adjusted_conf)
                except ValueError:
                    adjusted_conf = aggregated["confidence"]

            aggregated["confidence"] = max(0.0, min(1.0, adjusted_conf))
            aggregated["meta_judge"] = judge_result

    if triage_result:
        aggregated["triage"] = triage_result

    aggregated["analyst_details"] = {
        name: {k: v for k, v in res.items() if not k.startswith("_")}
        for name, res in analyst_results.items()
    }

    return aggregated


# ========== MAJORITY VOTE ENSEMBLE (multi-model) ==========


def classify_ensemble_majority_vote(
    transcript: str,
    models: dict[str, str] | None = None,
    use_meta_judge: bool = True,
) -> dict:
    """
    Run the ensemble pipeline across multiple models and take majority vote.
    This mirrors og-agent.py's multi-model aggregation pattern.
    """
    if models is None:
        models = ENSEMBLE_MODELS

    votes = []
    all_results = {}

    for label, model_id in models.items():
        result = classify_ensemble(transcript, model_id, use_meta_judge)
        votes.append(result["label"])
        all_results[label] = result

    high_risk_count = sum(1 for v in votes if v == "HIGH_RISK")
    majority_label = "HIGH_RISK" if high_risk_count > len(votes) / 2 else "NOT_HIGH_RISK"

    confidences = [r["confidence"] for r in all_results.values()]
    avg_confidence = sum(confidences) / len(confidences) if confidences else 0.5

    return {
        "label": majority_label,
        "confidence": avg_confidence,
        "individual_votes": {lbl: res["label"] for lbl, res in all_results.items()},
        "individual_confidences": {lbl: res["confidence"] for lbl, res in all_results.items()},
        "details": all_results,
    }


# ========== CONVENIENCE: CLASSIFY WITH BOTH APPROACHES ==========


def classify_transcript(
    transcript: str,
    model_id: str = DEFAULT_MODEL,
    approaches: list[str] | None = None,
) -> dict:
    """
    Run one or both classification approaches on a transcript.
    Returns results keyed by approach name.
    """
    if approaches is None:
        approaches = ["single_cot", "ensemble"]

    results = {}
    start = time.time()

    if "single_cot" in approaches:
        t0 = time.time()
        results["single_cot"] = classify_single_cot(transcript, model_id)
        results["single_cot"]["_latency_s"] = round(time.time() - t0, 2)

    if "ensemble" in approaches:
        t0 = time.time()
        results["ensemble"] = classify_ensemble(transcript, model_id)
        results["ensemble"]["_latency_s"] = round(time.time() - t0, 2)

    results["_total_latency_s"] = round(time.time() - start, 2)
    return results


# ========== MAIN (demo usage) ==========

if __name__ == "__main__":
    demo_transcript = """
Therapist: How have things been since we last spoke?

Patient: Honestly... not great. I stopped going to work three days ago. I just couldn't get out of bed. My roommate moved out last month and the apartment is so quiet now. I've been drinking more — not like blackout, but every night. It takes the edge off.

Therapist: That sounds like a really difficult stretch. What's the edge you're trying to take off?

Patient: I don't know. This feeling like... nothing matters? Like I could disappear and no one would notice for a week. My mom called but I didn't pick up. I've been letting everything go to voicemail.

Therapist: When you say disappear, can you tell me more about what you mean?

Patient: Not like... I'm not going to do anything. I just think about it sometimes. What it would be like to not wake up. It's not a plan. It's more like a wish. I looked up my life insurance policy last week though. Just to see if my sister would get it. That's not... I know how that sounds.

Therapist: I appreciate you telling me that. That takes courage.

Patient: Yeah. I just don't see how this gets better. Every job I've had, I mess up eventually. Every person leaves. I'm tired of starting over.
"""

    print("=" * 60)
    print("RISK CLASSIFICATION AGENT - DEMO")
    print("=" * 60)

    print("\n--- Approach 1: Single Chain-of-Thought ---")
    result_single = classify_single_cot(demo_transcript)
    print(f"Label: {result_single['label']}")
    print(f"Confidence: {result_single['confidence']}")
    print(f"Reasoning: {result_single.get('reasoning', 'N/A')}")
    print(f"Subcategories: {result_single.get('risk_subcategories', {})}")

    print("\n--- Approach 2: Ensemble ---")
    result_ensemble = classify_ensemble(demo_transcript)
    print(f"Label: {result_ensemble['label']}")
    print(f"Confidence: {result_ensemble['confidence']}")
    print(f"Reasoning: {result_ensemble.get('reasoning', 'N/A')}")
