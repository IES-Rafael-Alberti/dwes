#!/usr/bin/env python3
"""Plan and optionally operate GitHub repositories for DWES group work.

All mutating commands require --apply. The default is a dry-run plan.
"""

from __future__ import annotations

import argparse
import csv
import json
import subprocess
import sys
from pathlib import Path


def read_rows(path: Path) -> list[dict[str, str]]:
    with path.open(newline="", encoding="utf-8") as handle:
        rows = list(csv.DictReader(handle))
    required = {"grupo", "repo", "integrantes", "template", "visibilidad", "hito"}
    if not rows or not required.issubset(rows[0]):
        raise ValueError(f"CSV must contain columns: {', '.join(sorted(required))}")
    for row in rows:
        if not row["repo"] or not row["template"] or not row["integrantes"]:
            raise ValueError(f"Incomplete row for group {row.get('grupo', '?')}")
    return rows


def run_gh(args: list[str]) -> str:
    result = subprocess.run(["gh", *args], check=True, text=True, capture_output=True)
    return result.stdout


def repo_name(org: str, row: dict[str, str]) -> str:
    return f"{org}/{row['repo']}"


def plan(rows: list[dict[str, str]], org: str | None) -> None:
    for row in rows:
        target = repo_name(org, row) if org else row["repo"]
        users = ", ".join(user.strip() for user in row["integrantes"].split(";") if user.strip())
        print(f"{row['grupo']}: {target} <- {row['template']} [{row['visibilidad']}] [{row['hito']}]")
        print(f"  colaboradores: {users}")
        print("  acciones: crear repo, añadir colaboradores, labels, issue de entrega")


def create(rows: list[dict[str, str]], org: str, apply: bool) -> None:
    for row in rows:
        target = repo_name(org, row)
        visibility = "--public" if row["visibilidad"].lower() == "public" else "--private"
        commands = [
            ["repo", "create", target, "--template", row["template"], visibility],
            *[["api", f"repos/{target}/collaborators/{user.strip()}", "-X", "PUT",
               "-f", "permission=push"]
              for user in row["integrantes"].split(";") if user.strip()],
            ["label", "create", "hito", "--repo", target, "--force"],
            ["issue", "create", "--repo", target, "--title", f"Entrega {row['hito']}",
             "--body", "Revisar README, tests, evidencia y defensa."],
        ]
        for command in commands:
            printable = "gh " + " ".join(command)
            if not apply:
                print("DRY-RUN", printable)
            else:
                run_gh(command)
                print("OK", printable)


def report(rows: list[dict[str, str]], org: str | None, output: Path) -> None:
    fields = ["grupo", "repo", "contributors", "commits", "pull_requests", "issues", "releases"]
    with output.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=fields)
        writer.writeheader()
        for row in rows:
            target = repo_name(org, row) if org else row["repo"]
            commits = json.loads(run_gh(["api", f"repos/{target}/commits?per_page=100"]))
            prs = json.loads(run_gh(["api", f"repos/{target}/pulls?state=all&per_page=100"]))
            issues = json.loads(run_gh(["api", f"repos/{target}/issues?state=all&per_page=100"]))
            releases = json.loads(run_gh(["api", f"repos/{target}/releases?per_page=100"]))
            contributors = sorted({(c.get("author") or {}).get("login", "unknown") for c in commits})
            issues_without_prs = [issue for issue in issues if "pull_request" not in issue]
            writer.writerow({
                "grupo": row.get("grupo", ""),
                "repo": target,
                "contributors": ";".join(contributors),
                "commits": len(commits),
                "pull_requests": len(prs),
                "issues": len(issues_without_prs),
                "releases": len(releases),
            })


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="action", required=True)
    for name in ("plan", "create", "report"):
        command = sub.add_parser(name)
        command.add_argument("--csv", type=Path, required=True)
        command.add_argument("--org")
        command.add_argument("--apply", action="store_true")
        command.add_argument("--out", type=Path, default=Path("github-report.csv"))
    args = parser.parse_args()
    try:
        rows = read_rows(args.csv)
        if args.action == "plan":
            plan(rows, args.org)
        elif args.action == "create":
            if not args.org:
                raise ValueError("create requires --org")
            create(rows, args.org, args.apply)
        else:
            report(rows, args.org, args.out)
            print(f"Report written to {args.out}")
    except (OSError, ValueError, subprocess.CalledProcessError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
