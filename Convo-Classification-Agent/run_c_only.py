"""
Run a single category dataset through the ensemble.
Usage:
  python3 run_c_only.py A          # runs v3_A_explicit.json (90)
  python3 run_c_only.py B          # runs v3_B_implicit.json (90)
  python3 run_c_only.py C          # runs v3.1_category_C_final.json (90)
  python3 run_c_only.py C --limit 10
"""

import csv
import json
import time
import os
import argparse
from classification_agent import classify_ensemble, DEFAULT_MODEL
from benchmark import load_transcripts, chat_history_to_transcript

DATASET_MAP = {
    "A": "dataset v_3/v3_A_explicit.json",
    "B": "dataset v_3/v3_B_implicit.json",
    "C": "dataset v_3/v3.1_category_C_final.json",
}
OUTPUT_DIR = "evaluation_results"


def format_analyst_section(name, report):
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


def run(category="C", limit=90):
    dataset_path = DATASET_MAP[category.upper()]
    detail_file = os.path.join(OUTPUT_DIR, f"v3_{category.upper()}_detailed.txt")
    csv_file = os.path.join(OUTPUT_DIR, f"v3_{category.upper()}_results.csv")

    os.makedirs(OUTPUT_DIR, exist_ok=True)
    entries = load_transcripts(dataset_path)[:limit]
    print(f"Model: {DEFAULT_MODEL}")
    print(f"Dataset: {dataset_path}")
    print(f"Running {len(entries)} conversations")
    print(f"Detail: {detail_file}")
    print(f"CSV: {csv_file}")
    print("=" * 60)

    stats = {"correct": 0, "total": 0}

    with open(detail_file, "w", encoding="utf-8") as df, \
         open(csv_file, "w", newline="", encoding="utf-8") as cf:

        csv_writer = csv.writer(cf)
        csv_writer.writerow(["id", "predicted", "true_label", "confidence", "result"])

        df.write("=" * 80 + "\n")
        df.write("  C-ONLY EVALUATION (v3.1_category_C_final.json)\n")
        df.write(f"  Date: {time.strftime('%Y-%m-%d %H:%M:%S')}\n")
        df.write(f"  Model: {DEFAULT_MODEL}\n")
        df.write(f"  Samples: {len(entries)}\n")
        df.write("=" * 80 + "\n\n")

        for idx, entry in enumerate(entries):
            print(f"\n[{idx+1}/{len(entries)}] {entry['id']} (true: {entry['label']})")

            t0 = time.time()
            try:
                result = classify_ensemble(entry["transcript"], DEFAULT_MODEL)
                latency = round(time.time() - t0, 2)
            except Exception as e:
                print(f"  ERROR: {e}")
                result = {"label": "ERROR", "confidence": 0.0, "error": str(e)}
                latency = round(time.time() - t0, 2)

            pred = result.get("label", "ERROR")
            correct = pred == entry["label"]
            stats["total"] += 1
            if correct:
                stats["correct"] += 1

            res_str = "Correct" if correct else "Wrong"
            acc = stats["correct"] / stats["total"] * 100
            print(f"  → {pred} (conf={result.get('confidence',0):.2f}) [{res_str}] ({latency}s)")
            print(f"  Running: {stats['correct']}/{stats['total']} ({acc:.1f}%)")

            csv_writer.writerow([entry["id"], pred, entry["label"],
                                 f"{result.get('confidence',0):.2f}", res_str])
            cf.flush()

            # Detailed log
            lines = []
            lines.append("=" * 80)
            lines.append(f"  #{idx+1} | ID: {entry['id']} | True: {entry['label']}")
            lines.append("=" * 80)
            lines.append("")
            transcript_lines = entry["transcript"].split("\n")
            for tl in transcript_lines[:30]:
                if tl.strip():
                    lines.append(f"    {tl.strip()}")
            if len(transcript_lines) > 30:
                lines.append(f"    [...{len(transcript_lines)-30} more lines...]")
            lines.append("")

            analyst_details = result.get("analyst_details", {})
            if analyst_details:
                lines.append("  ANALYST ASSESSMENTS:")
                for name in ["linguistic", "emotional", "contextual", "behavioral"]:
                    if name in analyst_details:
                        lines.append(format_analyst_section(name, analyst_details[name]))
                        lines.append("")

            triage = result.get("triage")
            if triage:
                lines.append(f"  TRIAGE: routing={triage.get('routing','N/A')} | {triage.get('rationale','')}")
                lines.append("")

            # Topic grounding
            tg = result.get("topic_grounding")
            if tg:
                lines.append("  TOPIC GROUNDING:")
                lines.append(f"    subject: {tg.get('concrete_subject', 'N/A')}")
                lines.append(f"    stakes: {tg.get('actual_stakes', 'N/A')}")
                lines.append(f"    language_direction: {tg.get('language_direction', 'N/A')}")
                lines.append(f"    topic_is_mundane: {tg.get('topic_is_mundane', 'N/A')}")
                lines.append("")

            lines.append("  AGGREGATION:")
            cm = result.get("convergence_matrix")
            if cm:
                for cluster, data in cm.items():
                    if isinstance(data, dict):
                        count = data.get("count", 0)
                        analysts = data.get("contributing_analysts", [])
                        lines.append(f"    {cluster}: {count} ({', '.join(analysts)})")
            if "escalation_rule_triggered" in result:
                lines.append(f"    escalation_rule: {result['escalation_rule_triggered']}")
            if "reasoning" in result:
                lines.append(f"    reasoning: {result['reasoning']}")
            lines.append("")

            meta = result.get("meta_judge", {})
            if meta:
                lines.append("  META-JUDGE:")
                for k, v in meta.items():
                    lines.append(f"    {k}: {v}")
                lines.append("")

            marker = "✓ CORRECT" if correct else "✗ WRONG"
            lines.append(f"  RESULT: {pred} | {marker} | Running: {stats['correct']}/{stats['total']} ({acc:.1f}%)")
            lines.append("")
            lines.append("")

            df.write("\n".join(lines))
            df.flush()

        summary = f"\nFINAL: {stats['correct']}/{stats['total']} ({stats['correct']/stats['total']*100:.1f}%)\n"
        df.write(summary)
        print(summary)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run single category evaluation")
    parser.add_argument("category", nargs="?", default="C", choices=["A", "B", "C"],
                        help="Category to run (default: C)")
    parser.add_argument("--limit", type=int, default=90, help="Number of conversations to run")
    args = parser.parse_args()
    run(category=args.category, limit=args.limit)
