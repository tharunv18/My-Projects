"""
Evaluation runner for the ensemble classification agent.
Runs conversations from dataset v_3 in round-robin order (one from each category).
Outputs:
  1. A detailed .txt log with full logic/decisions at each step
  2. A simple .csv with ID,Predicted,TrueLabel,Result
Resumes from the last entry in the CSV to avoid re-running completed prompts.
"""

import csv
import json
import time
import os
from pathlib import Path
from itertools import zip_longest
from classification_agent import classify_ensemble, classify_ensemble_majority_vote, DEFAULT_MODEL
from benchmark import load_transcripts, normalize_label, chat_history_to_transcript

# ========== CONFIGURATION ==========

DATASET_DIR = "dataset v_3"
DATASET_FILES = [
    "v3_A_explicit.json",
    "v3_B_implicit.json",
    "v3_C_nonsensitive.json",
]
OUTPUT_DIR = "evaluation_results"
DETAIL_FILE = os.path.join(OUTPUT_DIR, "v3_detailed.txt")
CSV_FILE = os.path.join(OUTPUT_DIR, "v3_results.csv")
MODEL = DEFAULT_MODEL


# ========== LOADING ==========


def load_all_datasets():
    """Load all three category files and return them as separate lists."""
    datasets = {}
    for fname in DATASET_FILES:
        path = os.path.join(DATASET_DIR, fname)
        entries = load_transcripts(path)
        datasets[fname] = entries
        print(f"  {fname}: {len(entries)} conversations")
    return datasets


def interleave_datasets(datasets, limit_per_file=None):
    """Interleave entries round-robin: one from A, one from B, one from C, repeat."""
    lists = []
    for fname in DATASET_FILES:
        entries = datasets[fname]
        if limit_per_file:
            entries = entries[:limit_per_file]
        lists.append(entries)

    interleaved = []
    for group in zip_longest(*lists):
        for entry in group:
            if entry is not None:
                interleaved.append(entry)
    return interleaved


# ========== RESUME LOGIC ==========


def get_completed_ids():
    """Read the CSV and return the set of IDs already evaluated."""
    completed = set()
    if not os.path.exists(CSV_FILE):
        return completed
    with open(CSV_FILE, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            completed.add(row["id"])
    return completed


# ========== FORMATTING ==========


def format_analyst_section(name, report):
    """Format a single analyst's structured report into readable text."""
    lines = []
    lines.append(f"  {'─'*60}")
    lines.append(f"  {name.upper()} ANALYST")
    lines.append(f"  {'─'*60}")

    for key, value in report.items():
        if key.startswith("_") or key == "summary" or key.startswith("overall"):
            continue
        if isinstance(value, dict):
            level = value.get("level", "N/A")
            evidence = value.get("evidence", [])
            extra = ""
            for k, v in value.items():
                if k not in ("level", "evidence"):
                    extra += f" [{k}: {v}]"
            lines.append(f"    {key}: {level}{extra}")
            if evidence and level not in ("NOT_PRESENT",):
                for e in evidence[:3]:
                    lines.append(f"      → \"{e[:120]}\"")
        else:
            lines.append(f"    {key}: {value}")

    overall_key = f"overall_{name}_risk"
    if overall_key in report:
        lines.append(f"    OVERALL: {report[overall_key]}")
    if "summary" in report:
        lines.append(f"    Summary: {report['summary']}")

    return "\n".join(lines)


def format_result_entry(idx, entry, result, running_stats):
    """Format a single evaluation result into a readable block."""
    lines = []

    lines.append("=" * 80)
    lines.append(f"  EVALUATION #{idx + 1}")
    lines.append(f"  ID: {entry['id']}  |  Category: {entry.get('category', 'N/A')}  |  True Label: {entry['label']}")
    lines.append("=" * 80)

    # Transcript
    lines.append("")
    lines.append("  TRANSCRIPT:")
    lines.append("  " + "─" * 60)
    transcript_lines = entry["transcript"].split("\n")
    for tl in transcript_lines[:30]:
        if tl.strip():
            lines.append(f"    {tl.strip()}")
    if len(transcript_lines) > 30:
        lines.append(f"    [...{len(transcript_lines) - 30} more lines...]")
    lines.append("")

    # Analyst details
    analyst_details = result.get("analyst_details", {})
    if analyst_details:
        lines.append("  ANALYST ASSESSMENTS:")
        for name in ["linguistic", "emotional", "contextual", "behavioral"]:
            if name in analyst_details:
                lines.append(format_analyst_section(name, analyst_details[name]))
                lines.append("")

    # Triage
    triage = result.get("triage")
    if triage:
        lines.append(f"  {'─'*60}")
        lines.append("  TRIAGE GATE")
        lines.append(f"  {'─'*60}")
        lines.append(f"    routing: {triage.get('routing', 'N/A')}")
        lines.append(f"    rationale: {triage.get('rationale', 'N/A')}")
        lines.append("")

    # Topic grounding
    tg = result.get("topic_grounding")
    if tg:
        lines.append(f"  {'─'*60}")
        lines.append("  TOPIC GROUNDING")
        lines.append(f"  {'─'*60}")
        lines.append(f"    subject: {tg.get('concrete_subject', 'N/A')}")
        lines.append(f"    stakes: {tg.get('actual_stakes', 'N/A')}")
        lines.append(f"    language_direction: {tg.get('language_direction', 'N/A')}")
        lines.append(f"    topic_is_mundane: {tg.get('topic_is_mundane', 'N/A')}")
        lines.append("")

    # Aggregation
    lines.append(f"  {'─'*60}")
    lines.append("  AGGREGATION")
    lines.append(f"  {'─'*60}")

    cm = result.get("convergence_matrix")
    if cm:
        lines.append("    convergence_matrix:")
        for cluster, data in cm.items():
            if isinstance(data, dict):
                count = data.get("count", 0)
                analysts = data.get("contributing_analysts", [])
                lines.append(f"      {cluster}: {count} ({', '.join(analysts) if analysts else 'none'})")

    if "escalation_rule_triggered" in result:
        lines.append(f"    escalation_rule: {result['escalation_rule_triggered']}")

    for key in ["convergent_patterns", "surface_signal_mismatch", "strongest_evidence", "genuine_protective_factors"]:
        if key in result:
            val = result[key]
            if isinstance(val, list):
                lines.append(f"    {key}:")
                for item in val:
                    lines.append(f"      • {str(item)[:120]}")
            else:
                lines.append(f"    {key}: {val}")

    if "risk_subcategories" in result:
        lines.append(f"    risk_subcategories: {result['risk_subcategories']}")

    if "reasoning" in result:
        lines.append(f"    reasoning: {result['reasoning']}")

    # Meta-judge
    meta = result.get("meta_judge", {})
    if meta:
        lines.append("")
        lines.append(f"  {'─'*60}")
        lines.append("  META-JUDGE")
        lines.append(f"  {'─'*60}")
        for key, val in meta.items():
            lines.append(f"    {key}: {val}")

    # Final classification
    lines.append("")
    lines.append(f"  {'━'*60}")
    pred_label = result.get("label", "UNKNOWN")
    confidence = result.get("confidence", 0.0)
    correct = pred_label == entry["label"]
    marker = "✓ CORRECT" if correct else "✗ WRONG"

    lines.append(f"  PREDICTION: {pred_label}  (confidence: {confidence:.2f})")
    lines.append(f"  TRUE LABEL: {entry['label']}")
    lines.append(f"  RESULT:     {marker}")
    lines.append(f"  {'━'*60}")

    # Running accuracy
    lines.append("")
    lines.append(f"  RUNNING ACCURACY: {running_stats['correct']}/{running_stats['total']} "
                 f"({running_stats['correct']/running_stats['total']*100:.1f}%)")
    lines.append(f"    A (explicit):     {running_stats['a_correct']}/{running_stats['a_total']}")
    lines.append(f"    B (implicit):     {running_stats['b_correct']}/{running_stats['b_total']}")
    lines.append(f"    C (nonsensitive): {running_stats['c_correct']}/{running_stats['c_total']}")
    lines.append("")
    lines.append("")

    return "\n".join(lines)


# ========== RUNNER ==========


def run_evaluation(limit_per_file=None, model_id=None, multi_model=False):
    """Run the full evaluation, resuming from last CSV entry."""
    if model_id is None:
        model_id = MODEL

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    print(f"\nLoading datasets from: {DATASET_DIR}")
    datasets = load_all_datasets()
    entries = interleave_datasets(datasets, limit_per_file)
    total = len(entries)

    # Determine resume point
    completed_ids = get_completed_ids()
    if completed_ids:
        print(f"\nResuming: {len(completed_ids)} already completed, skipping those.")

    # Filter to only entries not yet done
    remaining = [e for e in entries if e["id"] not in completed_ids]
    print(f"Total conversations: {total}")
    print(f"Remaining to evaluate: {len(remaining)}")
    print(f"Model: {model_id}")
    print(f"Detail log: {DETAIL_FILE}")
    print(f"CSV results: {CSV_FILE}")
    print("=" * 60)

    if not remaining:
        print("All entries already evaluated. Nothing to do.")
        return

    # Build running stats from existing CSV
    running_stats = {
        "correct": 0, "total": 0,
        "a_correct": 0, "a_total": 0,
        "b_correct": 0, "b_total": 0,
        "c_correct": 0, "c_total": 0,
    }
    if os.path.exists(CSV_FILE):
        with open(CSV_FILE, "r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                running_stats["total"] += 1
                if row["result"] == "Correct":
                    running_stats["correct"] += 1
                cat = row["id"].split("-")[0].lower()
                if cat == "a":
                    running_stats["a_total"] += 1
                    if row["result"] == "Correct":
                        running_stats["a_correct"] += 1
                elif cat == "b":
                    running_stats["b_total"] += 1
                    if row["result"] == "Correct":
                        running_stats["b_correct"] += 1
                elif cat == "c":
                    running_stats["c_total"] += 1
                    if row["result"] == "Correct":
                        running_stats["c_correct"] += 1

    # Open files in append mode
    csv_exists = os.path.exists(CSV_FILE) and os.path.getsize(CSV_FILE) > 0
    csv_f = open(CSV_FILE, "a", newline="", encoding="utf-8")
    csv_writer = csv.writer(csv_f)
    if not csv_exists:
        csv_writer.writerow(["id", "predicted", "true_label", "confidence", "result"])

    detail_f = open(DETAIL_FILE, "a", encoding="utf-8")
    if not completed_ids:
        detail_f.write("=" * 80 + "\n")
        detail_f.write("  ENSEMBLE RISK CLASSIFICATION - EVALUATION RESULTS (v3)\n")
        detail_f.write(f"  Date: {time.strftime('%Y-%m-%d %H:%M:%S')}\n")
        detail_f.write(f"  Model: {model_id}\n")
        detail_f.write(f"  Dataset: {DATASET_DIR}\n")
        detail_f.write(f"  Total samples: {total}\n")
        detail_f.write("=" * 80 + "\n\n")

    eval_num = running_stats["total"]

    for entry in remaining:
        eval_num += 1
        print(f"\n[{eval_num}/{total}] Processing: {entry['id']} (true: {entry['label']})")

        t0 = time.time()
        try:
            if multi_model:
                result = classify_ensemble_majority_vote(entry["transcript"])
            else:
                result = classify_ensemble(entry["transcript"], model_id)
            latency = round(time.time() - t0, 2)
            result["_latency_s"] = latency
        except Exception as e:
            print(f"  ERROR: {e}")
            result = {"label": "ERROR", "confidence": 0.0, "error": str(e)}
            latency = round(time.time() - t0, 2)

        # Update running stats
        pred_label = result.get("label", "ERROR")
        correct = pred_label == entry["label"]
        running_stats["total"] += 1
        if correct:
            running_stats["correct"] += 1

        cat_id = entry["id"].split("-")[0].lower()
        if cat_id == "a":
            running_stats["a_total"] += 1
            if correct:
                running_stats["a_correct"] += 1
        elif cat_id == "b":
            running_stats["b_total"] += 1
            if correct:
                running_stats["b_correct"] += 1
        elif cat_id == "c":
            running_stats["c_total"] += 1
            if correct:
                running_stats["c_correct"] += 1

        marker = "CORRECT" if correct else "WRONG"
        result_str = "Correct" if correct else "Wrong"
        print(f"  → {pred_label} (conf={result.get('confidence', 0):.2f}) [{marker}] ({latency}s)")
        print(f"  Running: {running_stats['correct']}/{running_stats['total']} "
              f"({running_stats['correct']/running_stats['total']*100:.1f}%)")

        # Write CSV row
        csv_writer.writerow([
            entry["id"],
            pred_label,
            entry["label"],
            f"{result.get('confidence', 0.0):.2f}",
            result_str,
        ])
        csv_f.flush()

        # Write detailed log
        formatted = format_result_entry(eval_num - 1, entry, result, running_stats)
        detail_f.write(formatted)
        detail_f.flush()

        # Early stop if any category drops below 55% (after at least 5 samples)
        MIN_SAMPLES_BEFORE_CHECK = 5
        for cat_key, cat_name in [("a", "A (explicit)"), ("b", "B (implicit)"), ("c", "C (nonsensitive)")]:
            cat_total = running_stats[f"{cat_key}_total"]
            cat_correct = running_stats[f"{cat_key}_correct"]
            if cat_total >= MIN_SAMPLES_BEFORE_CHECK:
                cat_acc = cat_correct / cat_total
                if cat_acc < 0.40:
                    stop_msg = (
                        f"\n{'!'*60}\n"
                        f"  EARLY STOP: {cat_name} accuracy dropped to "
                        f"{cat_correct}/{cat_total} ({cat_acc*100:.1f}%) — below 55% threshold.\n"
                        f"  Halting evaluation.\n"
                        f"{'!'*60}\n"
                    )
                    print(stop_msg)
                    detail_f.write(stop_msg)
                    detail_f.close()
                    csv_f.close()
                    return running_stats

    # Write final summary to detail file
    summary = "\n" + "=" * 80 + "\n"
    summary += "  FINAL SUMMARY\n"
    summary += "=" * 80 + "\n"
    summary += f"  Total: {running_stats['correct']}/{running_stats['total']} "
    summary += f"({running_stats['correct']/running_stats['total']*100:.1f}%)\n"
    summary += f"  A (explicit):     {running_stats['a_correct']}/{running_stats['a_total']}"
    if running_stats["a_total"] > 0:
        summary += f" ({running_stats['a_correct']/running_stats['a_total']*100:.1f}%)"
    summary += "\n"
    summary += f"  B (implicit):     {running_stats['b_correct']}/{running_stats['b_total']}"
    if running_stats["b_total"] > 0:
        summary += f" ({running_stats['b_correct']/running_stats['b_total']*100:.1f}%)"
    summary += "\n"
    summary += f"  C (nonsensitive): {running_stats['c_correct']}/{running_stats['c_total']}"
    if running_stats["c_total"] > 0:
        summary += f" ({running_stats['c_correct']/running_stats['c_total']*100:.1f}%)"
    summary += "\n"
    summary += "=" * 80 + "\n"

    detail_f.write(summary)
    print(summary)

    detail_f.close()
    csv_f.close()

    print(f"\nDetailed log: {DETAIL_FILE}")
    print(f"CSV results:  {CSV_FILE}")


# ========== CLI ==========

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description="Run ensemble evaluation on dataset v_3")
    parser.add_argument("--limit", type=int, default=None,
                        help="Limit per file (e.g., --limit 10 runs 10 from each = 30 total)")
    parser.add_argument("--model", default=DEFAULT_MODEL,
                        help="Model to use")
    parser.add_argument("--multi-model", action="store_true",
                        help="Use multi-model majority vote (runs multiple models)")
    args = parser.parse_args()

    run_evaluation(limit_per_file=args.limit, model_id=args.model, multi_model=args.multi_model)
