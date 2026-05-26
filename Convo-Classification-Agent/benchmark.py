"""
Benchmark harness for the Risk Classification Agent.
Loads labeled transcripts, runs both classification approaches,
computes accuracy/precision/recall/F1/confusion matrix, and logs predictions.
"""
import os
import json
import time
import argparse
from pathlib import Path
from collections import defaultdict

from classification_agent import classify_single_cot, classify_ensemble, DEFAULT_MODEL

# ========== LABEL MAPPING ==========

LABEL_MAP = {
    "SENSITIVE": "HIGH_RISK",
    "NOT_SENSITIVE": "NOT_HIGH_RISK",
    "HIGH_RISK": "HIGH_RISK",
    "NOT_HIGH_RISK": "NOT_HIGH_RISK",
}


def normalize_label(raw_label: str) -> str:
    """Map dataset labels to internal HIGH_RISK / NOT_HIGH_RISK."""
    key = raw_label.strip().upper().replace(" ", "_")
    return LABEL_MAP.get(key, key)


# ========== TRANSCRIPT FORMATTING ==========


def chat_history_to_transcript(chat_history: list[dict]) -> str:
    """Convert a list of {role, content} messages into a readable transcript string."""
    role_map = {"user": "Patient", "assistant": "Therapist", "system": "System"}
    lines = []
    for i, msg in enumerate(chat_history, 1):
        speaker = role_map.get(msg["role"], msg["role"].capitalize())
        lines.append(f"[Turn {i}] {speaker}: {msg['content']}")
    return "\n\n".join(lines)


# ========== DATASET LOADING ==========


def load_transcripts(file_path: str) -> list[dict]:
    """
    Load transcripts from a JSON file. Supports multiple layouts:

    Layout A - Category file (category_A/B/C format):
        {"category": "A", "total": 1000, "conversations": [
            {"id": "A-1", "label": "SENSITIVE", "chat_history": [...], ...}
        ]}

    Layout B - Flat JSON array:
        [{"transcript": "...", "label": "HIGH_RISK"}, ...]

    Layout C - Subfolder-based:
        data/high_risk/*.txt + data/not_high_risk/*.txt

    Layout D - Flat folder + labels.json:
        data/transcripts/*.txt + data/labels.json

    Returns list of {"id": str, "transcript": str, "label": str, "category": str}
    """
    path = Path(file_path)

    if path.suffix == ".json":
        with open(path, encoding="utf-8") as f:
            data = json.load(f)

        # Layout A: category file with "conversations" key
        if isinstance(data, dict) and "conversations" in data:
            entries = []
            for conv in data["conversations"]:
                transcript = chat_history_to_transcript(conv["chat_history"])
                entries.append({
                    "id": conv["id"],
                    "transcript": transcript,
                    "label": normalize_label(conv["label"]),
                    "category": conv.get("category", data.get("category", "")),
                    "scenario": conv.get("scenario", ""),
                })
            return entries

        # Layout B: flat array
        if isinstance(data, list):
            entries = []
            for i, item in enumerate(data):
                if "chat_history" in item:
                    transcript = chat_history_to_transcript(item["chat_history"])
                else:
                    transcript = item["transcript"]
                entries.append({
                    "id": item.get("id", f"sample_{i:04d}"),
                    "transcript": transcript,
                    "label": normalize_label(item["label"]),
                    "category": item.get("category", ""),
                })
            return entries

    # Layout C: subfolder-based labels
    if path.is_dir():
        high_risk_dir = path / "high_risk"
        not_high_risk_dir = path / "not_high_risk"
        if high_risk_dir.exists() and not_high_risk_dir.exists():
            entries = []
            for txt_file in sorted(high_risk_dir.glob("*.txt")):
                entries.append({
                    "id": txt_file.stem,
                    "transcript": txt_file.read_text(encoding="utf-8"),
                    "label": "HIGH_RISK",
                    "category": "",
                })
            for txt_file in sorted(not_high_risk_dir.glob("*.txt")):
                entries.append({
                    "id": txt_file.stem,
                    "transcript": txt_file.read_text(encoding="utf-8"),
                    "label": "NOT_HIGH_RISK",
                    "category": "",
                })
            return entries

        # Layout D: flat folder + labels.json
        labels_file = path / "labels.json"
        transcripts_dir = path / "transcripts"
        if labels_file.exists() and transcripts_dir.exists():
            with open(labels_file) as f:
                labels = json.load(f)
            entries = []
            for txt_file in sorted(transcripts_dir.glob("*.txt")):
                file_id = txt_file.stem
                if file_id in labels:
                    entries.append({
                        "id": file_id,
                        "transcript": txt_file.read_text(encoding="utf-8"),
                        "label": normalize_label(labels[file_id]),
                        "category": "",
                    })
            return entries

    raise ValueError(
        f"Could not determine dataset layout for '{file_path}'. "
        "Expected a category JSON, flat JSON array, "
        "subfolders (high_risk/, not_high_risk/), or flat dir + labels.json."
    )


def load_multiple_datasets(file_paths: list[str]) -> list[dict]:
    """Load and merge multiple dataset files."""
    all_entries = []
    for fp in file_paths:
        entries = load_transcripts(fp)
        print(f"  Loaded {len(entries)} from {Path(fp).name}")
        all_entries.extend(entries)
    return all_entries


# ========== METRICS COMPUTATION ==========


def compute_metrics(y_true: list[str], y_pred: list[str]) -> dict:
    """Compute accuracy, precision, recall, F1, and confusion matrix."""
    tp = sum(1 for t, p in zip(y_true, y_pred) if t == "HIGH_RISK" and p == "HIGH_RISK")
    tn = sum(1 for t, p in zip(y_true, y_pred) if t == "NOT_HIGH_RISK" and p == "NOT_HIGH_RISK")
    fp = sum(1 for t, p in zip(y_true, y_pred) if t == "NOT_HIGH_RISK" and p == "HIGH_RISK")
    fn = sum(1 for t, p in zip(y_true, y_pred) if t == "HIGH_RISK" and p == "NOT_HIGH_RISK")

    total = len(y_true)
    accuracy = (tp + tn) / total if total > 0 else 0.0
    precision = tp / (tp + fp) if (tp + fp) > 0 else 0.0
    recall = tp / (tp + fn) if (tp + fn) > 0 else 0.0
    f1 = 2 * precision * recall / (precision + recall) if (precision + recall) > 0 else 0.0

    return {
        "accuracy": round(accuracy, 4),
        "precision": round(precision, 4),
        "recall": round(recall, 4),
        "f1": round(f1, 4),
        "confusion_matrix": {
            "true_positive": tp,
            "true_negative": tn,
            "false_positive": fp,
            "false_negative": fn,
        },
        "total_samples": total,
        "positive_samples": tp + fn,
        "negative_samples": tn + fp,
    }


def compute_metrics_at_threshold(
    entries: list[dict],
    confidence_key: str,
    threshold: float,
) -> dict:
    """Compute metrics using a confidence threshold instead of the model's own label."""
    y_true = [e["label"] for e in entries]
    y_pred = [
        "HIGH_RISK" if e[confidence_key] >= threshold else "NOT_HIGH_RISK"
        for e in entries
    ]
    metrics = compute_metrics(y_true, y_pred)
    metrics["threshold"] = threshold
    return metrics


# ========== BENCHMARK RUNNER ==========


def run_benchmark(
    dataset_paths: list[str] | str,
    approaches: list[str] | None = None,
    model_id: str = DEFAULT_MODEL,
    output_dir: str = "benchmark_results",
    limit: int | None = None,
    offset: int = 0,
) -> dict:
    """
    Run the full benchmark: load data, classify, compute metrics, save results.
    Accepts a single path or a list of paths (for multiple category files).
    offset/limit apply per-file when multiple files are provided.
    """
    if approaches is None:
        approaches = ["single_cot", "ensemble"]

    if isinstance(dataset_paths, str):
        dataset_paths = [dataset_paths]

    print(f"\nLoading datasets...")
    if limit or offset:
        entries = []
        for fp in dataset_paths:
            file_entries = load_transcripts(fp)
            sliced = file_entries[offset:offset + limit] if limit else file_entries[offset:]
            entries.extend(sliced)
            print(f"  Loaded {len(sliced)} (indices {offset}-{offset + len(sliced) - 1}) from {Path(fp).name}")
    else:
        entries = load_multiple_datasets(dataset_paths)

    label_counts = defaultdict(int)
    cat_counts = defaultdict(int)
    for e in entries:
        label_counts[e["label"]] += 1
        if e.get("category"):
            cat_counts[e["category"]] += 1

    print(f"\nTotal: {len(entries)} transcripts")
    for lbl, cnt in sorted(label_counts.items()):
        print(f"  {lbl}: {cnt}")
    if cat_counts:
        print("  By category:")
        for cat, cnt in sorted(cat_counts.items()):
            print(f"    {cat}: {cnt}")
    print(f"Approaches: {approaches}")
    print(f"Model: {model_id}")
    print("=" * 60)

    predictions = defaultdict(list)
    all_results = []

    for i, entry in enumerate(entries):
        print(f"\n[{i+1}/{len(entries)}] Processing: {entry['id']} (true: {entry['label']})")
        entry_result = {"id": entry["id"], "true_label": entry["label"]}

        if "single_cot" in approaches:
            t0 = time.time()
            try:
                result = classify_single_cot(entry["transcript"], model_id)
                latency = round(time.time() - t0, 2)
                pred_label = result["label"]
                confidence = result["confidence"]
                entry_result["single_cot_pred"] = pred_label
                entry_result["single_cot_confidence"] = confidence
                entry_result["single_cot_reasoning"] = result.get("reasoning", "")
                entry_result["single_cot_latency"] = latency
                predictions["single_cot"].append({
                    "label": entry["label"],
                    "pred": pred_label,
                    "confidence": confidence,
                })
                match = "CORRECT" if pred_label == entry["label"] else "WRONG"
                print(f"  [single_cot] {pred_label} (conf={confidence:.2f}) [{match}] ({latency}s)")
            except Exception as e:
                print(f"  [single_cot] ERROR: {e}")
                entry_result["single_cot_error"] = str(e)
                predictions["single_cot"].append({
                    "label": entry["label"],
                    "pred": "ERROR",
                    "confidence": 0.0,
                })

        if "ensemble" in approaches:
            t0 = time.time()
            try:
                result = classify_ensemble(entry["transcript"], model_id)
                latency = round(time.time() - t0, 2)
                pred_label = result["label"]
                confidence = result["confidence"]
                entry_result["ensemble_pred"] = pred_label
                entry_result["ensemble_confidence"] = confidence
                entry_result["ensemble_reasoning"] = result.get("reasoning", "")
                entry_result["ensemble_latency"] = latency
                predictions["ensemble"].append({
                    "label": entry["label"],
                    "pred": pred_label,
                    "confidence": confidence,
                })
                match = "CORRECT" if pred_label == entry["label"] else "WRONG"
                print(f"  [ensemble]   {pred_label} (conf={confidence:.2f}) [{match}] ({latency}s)")
            except Exception as e:
                print(f"  [ensemble] ERROR: {e}")
                entry_result["ensemble_error"] = str(e)
                predictions["ensemble"].append({
                    "label": entry["label"],
                    "pred": "ERROR",
                    "confidence": 0.0,
                })

        all_results.append(entry_result)

    # Compute final metrics
    print("\n" + "=" * 60)
    print("BENCHMARK RESULTS")
    print("=" * 60)

    summary = {}
    for approach in approaches:
        preds = predictions[approach]
        valid_preds = [p for p in preds if p["pred"] != "ERROR"]
        if not valid_preds:
            print(f"\n[{approach}] No valid predictions.")
            continue

        y_true = [p["label"] for p in valid_preds]
        y_pred = [p["pred"] for p in valid_preds]
        metrics = compute_metrics(y_true, y_pred)
        summary[approach] = metrics

        print(f"\n--- {approach.upper()} ---")
        print(f"  Accuracy:  {metrics['accuracy']:.4f}")
        print(f"  Precision: {metrics['precision']:.4f}")
        print(f"  Recall:    {metrics['recall']:.4f}")
        print(f"  F1 Score:  {metrics['f1']:.4f}")
        print(f"  Confusion Matrix:")
        cm = metrics["confusion_matrix"]
        print(f"    TP={cm['true_positive']} FP={cm['false_positive']}")
        print(f"    FN={cm['false_negative']} TN={cm['true_negative']}")
        print(f"  Errors: {len(preds) - len(valid_preds)}/{len(preds)}")

    # Save detailed results
    os.makedirs(output_dir, exist_ok=True)
    timestamp = time.strftime("%Y%m%d_%H%M%S")

    results_file = os.path.join(output_dir, f"results_{timestamp}.json")
    with open(results_file, "w") as f:
        json.dump({
            "config": {
                "datasets": dataset_paths,
                "model": model_id,
                "approaches": approaches,
                "num_samples": len(entries),
            },
            "summary": summary,
            "predictions": all_results,
        }, f, indent=2)
    print(f"\nDetailed results saved to: {results_file}")

    summary["_results_file"] = results_file
    return summary


# ========== THRESHOLD SWEEP ==========


def sweep_thresholds(
    results_file: str,
    approach: str = "single_cot",
    thresholds: list[float] | None = None,
) -> list[dict]:
    """
    Sweep confidence thresholds on existing predictions to find optimal cutoff.
    Reads from a results JSON file produced by run_benchmark.
    """
    if thresholds is None:
        thresholds = [i / 20 for i in range(1, 20)]  # 0.05 to 0.95

    with open(results_file) as f:
        data = json.load(f)

    predictions = data["predictions"]
    conf_key = f"{approach}_confidence"
    pred_entries = []
    for p in predictions:
        if conf_key in p:
            pred_entries.append({
                "label": p["true_label"],
                conf_key: p[conf_key],
            })

    if not pred_entries:
        print(f"No predictions found for approach '{approach}'")
        return []

    print(f"\nThreshold sweep for '{approach}' ({len(pred_entries)} samples)")
    print("-" * 50)
    print(f"{'Threshold':>10} {'Accuracy':>10} {'Precision':>10} {'Recall':>8} {'F1':>8}")
    print("-" * 50)

    sweep_results = []
    best_f1 = 0
    best_threshold = 0.5

    for t in thresholds:
        metrics = compute_metrics_at_threshold(pred_entries, conf_key, t)
        sweep_results.append(metrics)
        print(f"{t:>10.2f} {metrics['accuracy']:>10.4f} {metrics['precision']:>10.4f} {metrics['recall']:>8.4f} {metrics['f1']:>8.4f}")
        if metrics["f1"] > best_f1:
            best_f1 = metrics["f1"]
            best_threshold = t

    print("-" * 50)
    print(f"Best threshold: {best_threshold:.2f} (F1={best_f1:.4f})")
    return sweep_results


# ========== CLI ==========


def main():
    parser = argparse.ArgumentParser(description="Benchmark the Risk Classification Agent")
    parser.add_argument(
        "datasets",
        nargs="+",
        help="Path(s) to dataset file(s). Pass multiple files to merge them.",
    )
    parser.add_argument(
        "--approaches",
        nargs="+",
        choices=["single_cot", "ensemble"],
        default=["ensemble"],
        help="Which approaches to benchmark",
    )
    parser.add_argument(
        "--model",
        default=DEFAULT_MODEL,
        help=f"Model to use (default: {DEFAULT_MODEL})",
    )
    parser.add_argument(
        "--output-dir",
        default="benchmark_results",
        help="Directory to save results",
    )
    parser.add_argument(
        "--limit",
        type=int,
        default=None,
        help="Limit number of samples per file",
    )
    parser.add_argument(
        "--offset",
        type=int,
        default=0,
        help="Skip first N samples per file (use with --limit to paginate)",
    )
    parser.add_argument(
        "--sweep",
        action="store_true",
        help="Run threshold sweep after benchmark",
    )

    args = parser.parse_args()

    summary = run_benchmark(
        dataset_paths=args.datasets,
        approaches=args.approaches,
        model_id=args.model,
        output_dir=args.output_dir,
        limit=args.limit,
        offset=args.offset,
    )

    if args.sweep and "_results_file" in summary:
        for approach in args.approaches:
            sweep_thresholds(summary["_results_file"], approach)


if __name__ == "__main__":
    main()
