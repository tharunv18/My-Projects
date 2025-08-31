import argparse
import numpy as np
from datasets import load_dataset
import sys, inspect, transformers
from transformers import (
    AutoConfig,
    AutoTokenizer,
    AutoModelForSequenceClassification,
    Trainer,
    TrainingArguments,
    EarlyStoppingCallback,
)

print("TRANSFORMERS FILE:", transformers.__file__)
print("TRANSFORMERS VERSION:", transformers.__version__)
print("HAS evaluation_strategy?:", "evaluation_strategy" in inspect.signature(transformers.TrainingArguments.__init__).parameters)

def get_args():
    ap = argparse.ArgumentParser()
    ap.add_argument("--dataset", type=str, required=True,
    help=r"C:\Users\venki\OneDrive\Desktop\reddit-therapy-scraper\Agent\RL_Agent\ctrs_reward_dataset.jsonl")

    ap.add_argument("--output_dir", type=str, default="./ctrs_reward_model")
    ap.add_argument("--model_name", type=str, default="distilbert-base-uncased")
    ap.add_argument("--epochs", type=int, default=3)
    ap.add_argument("--batch_size", type=int, default=16)
    ap.add_argument("--lr", type=float, default=2e-5)
    ap.add_argument("--max_length", type=int, default=256)
    ap.add_argument("--seed", type=int, default=42)
    ap.add_argument("--eval_steps", type=int, default=None, help="If set, use steps-based eval/save with this interval")
    ap.add_argument("--push_to_hub", action="store_true")
    return ap.parse_args()

def compute_metrics(eval_pred):
    preds, labels = eval_pred
    preds = preds.squeeze() * 60.0
    labels = labels * 60.0
    mae  = float(np.mean(np.abs(preds - labels)))
    rmse = float(np.sqrt(np.mean((preds - labels) ** 2)))
    return {"mae": mae, "rmse": rmse}

def main():
    args = get_args()

    # Load JSONL with {"text": "...", "reward": float}
    data = load_dataset("json", data_files=args.dataset, split="train")
    data = data.train_test_split(test_size=0.1, seed=args.seed)

    data = data.map(lambda x: {"reward": x["reward"] / 60.0})

    tokenizer = AutoTokenizer.from_pretrained(args.model_name)

    def tok(batch):
        return tokenizer(
            batch["text"],
            truncation=True,
            max_length=args.max_length,
            padding="max_length",
        )

    tokenized = data.map(tok, batched=True, remove_columns=[c for c in data["train"].column_names if c not in ["text","reward"]])
    tokenized = tokenized.rename_column("reward", "labels")
    tokenized.set_format(type="torch", columns=["input_ids", "attention_mask", "labels"])

    config = AutoConfig.from_pretrained(args.model_name, num_labels=1)
    config.problem_type = "regression"

    model = AutoModelForSequenceClassification.from_pretrained(args.model_name, config=config)

    # Determine eval/save strategy
    if args.eval_steps:
        evaluation_strategy="steps"
        save_strategy="steps"
        save_steps=args.eval_steps
        eval_steps=args.eval_steps
    else:
        evaluation_strategy="epoch"
        save_strategy="epoch"
        save_steps=None
        eval_steps=None

    training_args = TrainingArguments(
        output_dir=args.output_dir,
        learning_rate=args.lr,
        per_device_train_batch_size=args.batch_size,
        per_device_eval_batch_size=args.batch_size,
        num_train_epochs=args.epochs,
        eval_strategy=evaluation_strategy,
        save_strategy=save_strategy,
        save_steps=save_steps,
        eval_steps=eval_steps,
        load_best_model_at_end=True,
        metric_for_best_model="rmse",
        greater_is_better=False,
        save_total_limit=2,
        logging_dir=f"{args.output_dir}/logs",
        logging_steps=50,
        seed=args.seed,
        report_to=["none"],
        push_to_hub=args.push_to_hub,
        fp16=False,                  # you're on CPU
        weight_decay=0.01,
        warmup_ratio=0.1,
        lr_scheduler_type="cosine",
        dataloader_pin_memory=False,
    )

    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=tokenized["train"],
        eval_dataset=tokenized["test"],
        compute_metrics=compute_metrics,
        callbacks=[EarlyStoppingCallback(early_stopping_patience=2)],
    )

    trainer.train()

    # Save final model locally
    trainer.save_model(args.output_dir)
    tokenizer.save_pretrained(args.output_dir)

    # Quick example: score a line after training
    example = "I feel like I'm always failing [THERAPIST] Let's explore what 'always' means here—what happened most recently?"
    inputs = tokenizer(example, return_tensors="pt", truncation=True, max_length=args.max_length, padding=True)
    with np.errstate(all='ignore'):
        pred = trainer.model(**{k: v for k, v in inputs.items() if k in ["input_ids", "attention_mask"]}).logits.detach().cpu().numpy().squeeze().item()
    print(f"Example score: {pred:.3f}")

if __name__ == "__main__":
    main()
