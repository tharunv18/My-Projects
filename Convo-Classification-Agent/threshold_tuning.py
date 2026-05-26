"""
Threshold tuning module for the Risk Classification Agent.
Finds optimal confidence thresholds using validation data to maximize F1.

Usage:
    # After running benchmark.py and getting a results JSON:
    python threshold_tuning.py benchmark_results/results_XXXXXX.json

    # Or directly on a dataset (runs classification + tuning):
    python threshold_tuning.py --dataset path/to/data --split 0.3
"""

import json
import argparse
import random
from pathlib import Path

from benchmark import (
    load_transcripts,
    load_multiple_datasets,
    compute_metrics,
    run_benchmark,
)


def split_dataset(
    entries: list[dict],
    val_ratio: float = 0.3,
    seed: int = 42,
) -> tuple[list[dict], list[dict]]:
    """Split entries into train and validation sets with stratification."""
    random.seed(seed)

    high_risk = [e for e in entries if e["label"] == "HIGH_RISK"]
    not_high_risk = [e for e in entries if e["label"] == "NOT_HIGH_RISK"]

    random.shuffle(high_risk)
    random.shuffle(not_high_risk)

    val_hr = high_risk[:int(len(high_risk) * val_ratio)]
    train_hr = high_risk[int(len(high_risk) * val_ratio):]

    val_nhr = not_high_risk[:int(len(not_high_risk) * val_ratio)]
    train_nhr = not_high_risk[int(len(not_high_risk) * val_ratio):]

    train = train_hr + train_nhr
    val = val_hr + val_nhr

    random.shuffle(train)
    random.shuffle(val)

    return train, val


def find_optimal_threshold(
    predictions: list[dict],
    approach: str,
    metric: str = "f1",
    granularity: int = 100,
) -> dict:
    """
    Find the optimal confidence threshold for a given approach.
    Sweeps thresholds from 0.01 to 0.99 and returns the one that maximizes
    the chosen metric (default: F1).

    predictions: list of dicts with keys 'true_label' and '{approach}_confidence'
    """
    conf_key = f"{approach}_confidence"
    valid = [p for p in predictions if conf_key in p and p[conf_key] is not None]

    if not valid:
        return {"optimal_threshold": 0.5, "best_metric_value": 0.0, "metric": metric}

    thresholds = [i / granularity for i in range(1, granularity)]
    best_value = -1.0
    best_threshold = 0.5
    all_results = []

    for threshold in thresholds:
        y_true = [p["true_label"] for p in valid]
        y_pred = [
            "HIGH_RISK" if p[conf_key] >= threshold else "NOT_HIGH_RISK"
            for p in valid
        ]
        metrics = compute_metrics(y_true, y_pred)
        metric_value = metrics[metric]

        all_results.append({
            "threshold": round(threshold, 4),
            "accuracy": metrics["accuracy"],
            "precision": metrics["precision"],
            "recall": metrics["recall"],
            "f1": metrics["f1"],
        })

        if metric_value > best_value:
            best_value = metric_value
            best_threshold = threshold

    # Also find thresholds optimizing other metrics for comparison
    best_by_metric = {}
    for m in ["accuracy", "precision", "recall", "f1"]:
        best_for_m = max(all_results, key=lambda x: x[m])
        best_by_metric[m] = {
            "threshold": best_for_m["threshold"],
            "value": best_for_m[m],
        }

    return {
        "optimal_threshold": round(best_threshold, 4),
        "best_metric_value": round(best_value, 4),
        "metric": metric,
        "best_by_all_metrics": best_by_metric,
        "sweep_results": all_results,
        "num_samples": len(valid),
    }


def tune_from_results_file(results_file: str, metric: str = "f1") -> dict:
    """
    Load benchmark results and find optimal thresholds for each approach.
    """
    with open(results_file) as f:
        data = json.load(f)

    predictions = data["predictions"]
    approaches = data["config"]["approaches"]
    tuning_results = {}

    for approach in approaches:
        conf_key = f"{approach}_confidence"
        valid_preds = [
            {"true_label": p["true_label"], conf_key: p.get(conf_key)}
            for p in predictions
            if conf_key in p
        ]

        if not valid_preds:
            continue

        result = find_optimal_threshold(valid_preds, approach, metric)
        tuning_results[approach] = result

        print(f"\n{'='*50}")
        print(f"THRESHOLD TUNING: {approach.upper()}")
        print(f"{'='*50}")
        print(f"Optimizing for: {metric}")
        print(f"Samples: {result['num_samples']}")
        print(f"Optimal threshold: {result['optimal_threshold']}")
        print(f"Best {metric}: {result['best_metric_value']}")
        print(f"\nBest thresholds by metric:")
        for m, info in result["best_by_all_metrics"].items():
            print(f"  {m:>10}: threshold={info['threshold']:.4f} value={info['value']:.4f}")

    return tuning_results


def tune_from_dataset(
    dataset_path: str,
    model_id: str = "deepseek-ai/DeepSeek-V3",
    val_ratio: float = 0.3,
    approaches: list[str] | None = None,
    metric: str = "f1",
) -> dict:
    """
    End-to-end: load dataset, split into train/val, run classification on val,
    find optimal thresholds.
    """
    if approaches is None:
        approaches = ["single_cot", "ensemble"]

    print(f"Loading dataset: {dataset_path}")
    entries = load_transcripts(dataset_path)
    train, val = split_dataset(entries, val_ratio)

    print(f"Total: {len(entries)} | Train: {len(train)} | Val: {len(val)}")
    print(f"Val HIGH_RISK: {sum(1 for e in val if e['label'] == 'HIGH_RISK')}")
    print(f"Val NOT_HIGH_RISK: {sum(1 for e in val if e['label'] == 'NOT_HIGH_RISK')}")

    # Save val set temporarily for benchmark
    val_data = [{"id": e["id"], "transcript": e["transcript"], "label": e["label"]} for e in val]
    val_file = Path("benchmark_results") / "val_split.json"
    val_file.parent.mkdir(exist_ok=True)
    with open(val_file, "w") as f:
        json.dump(val_data, f, indent=2)

    # Run benchmark on validation set
    summary = run_benchmark(
        dataset_path=str(val_file),
        approaches=approaches,
        model_id=model_id,
        output_dir="benchmark_results",
    )

    # Find latest results file
    results_dir = Path("benchmark_results")
    results_files = sorted(results_dir.glob("results_*.json"), reverse=True)
    if not results_files:
        print("ERROR: No results file found after benchmark.")
        return {}

    tuning_results = tune_from_results_file(str(results_files[0]), metric)

    # Save tuning config for production use
    config_file = results_dir / "optimal_thresholds.json"
    config = {
        "model": model_id,
        "val_ratio": val_ratio,
        "metric_optimized": metric,
        "thresholds": {
            approach: result["optimal_threshold"]
            for approach, result in tuning_results.items()
        },
    }
    with open(config_file, "w") as f:
        json.dump(config, f, indent=2)
    print(f"\nOptimal thresholds saved to: {config_file}")

    return tuning_results


# ========== PRODUCTION HELPER ==========


def load_optimal_threshold(
    approach: str = "single_cot",
    config_path: str = "benchmark_results/optimal_thresholds.json",
) -> float:
    """Load the tuned threshold for production use."""
    try:
        with open(config_path) as f:
            config = json.load(f)
        return config["thresholds"].get(approach, 0.5)
    except (FileNotFoundError, json.JSONDecodeError, KeyError):
        return 0.5


def classify_with_tuned_threshold(
    transcript: str,
    approach: str = "single_cot",
    model_id: str = "deepseek-ai/DeepSeek-V3",
    threshold_config: str = "benchmark_results/optimal_thresholds.json",
) -> dict:
    """
    Classify using the tuned threshold instead of the model's raw label.
    This overrides the model's own HIGH_RISK/NOT_HIGH_RISK decision
    with the empirically optimal confidence threshold.
    """
    from classification_agent import classify_single_cot, classify_ensemble

    threshold = load_optimal_threshold(approach, threshold_config)

    if approach == "single_cot":
        result = classify_single_cot(transcript, model_id)
    elif approach == "ensemble":
        result = classify_ensemble(transcript, model_id)
    else:
        raise ValueError(f"Unknown approach: {approach}")

    raw_label = result["label"]
    confidence = result["confidence"]
    tuned_label = "HIGH_RISK" if confidence >= threshold else "NOT_HIGH_RISK"

    result["raw_label"] = raw_label
    result["label"] = tuned_label
    result["threshold_used"] = threshold
    result["threshold_overrode"] = (raw_label != tuned_label)

    return result


# ========== CLI ==========


def main():
    parser = argparse.ArgumentParser(description="Threshold tuning for Risk Classification Agent")
    subparsers = parser.add_subparsers(dest="command")

    # Tune from existing results file
    from_results = subparsers.add_parser("from-results", help="Tune from existing benchmark results")
    from_results.add_argument("results_file", help="Path to results JSON from benchmark.py")
    from_results.add_argument("--metric", default="f1", choices=["accuracy", "precision", "recall", "f1"])

    # Tune from dataset (end-to-end)
    from_dataset = subparsers.add_parser("from-dataset", help="Run end-to-end tuning on a dataset")
    from_dataset.add_argument("dataset", help="Path to labeled dataset")
    from_dataset.add_argument("--model", default="deepseek-ai/DeepSeek-V3")
    from_dataset.add_argument("--val-ratio", type=float, default=0.3)
    from_dataset.add_argument("--metric", default="f1", choices=["accuracy", "precision", "recall", "f1"])
    from_dataset.add_argument("--approaches", nargs="+", default=["single_cot", "ensemble"])

    args = parser.parse_args()

    if args.command == "from-results":
        tune_from_results_file(args.results_file, args.metric)
    elif args.command == "from-dataset":
        tune_from_dataset(
            dataset_path=args.dataset,
            model_id=args.model,
            val_ratio=args.val_ratio,
            approaches=args.approaches,
            metric=args.metric,
        )
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
