# Claude Pick Workflow

Claude can autonomously pick an open GitHub issue, implement it, and open a draft PR — all from a local Claude Code session.

## Prerequisites

- `gh` CLI authenticated — run `gh auth status` to verify
- Git push access to `nemerosa/ontrack`

## Labeling issues

Add the `claude-pick` label to any open issue in GitHub to make it eligible.
Issues that already have an assignee are skipped.

## Triggering

In any Claude Code session at the repo root, say:

> "Pick a claude-pick ticket and implement it"

To target a specific issue:

> "Pick issue #1234 and implement it"

## What Claude does

1. Lists open, unassigned issues labeled `claude-pick` via `gh issue list`
2. Picks the first candidate — stops and reports if none are found
3. Comments on the issue: "Picking up this issue, a draft PR will follow"
4. Creates a branch: `claude/issue-{N}-{short-slug}`
5. Reads the issue, reads `CLAUDE.md`, explores the relevant codebase
6. Implements the change following project conventions (backend, frontend, tests, DB migrations as needed)
7. If the issue is too vague or too large to implement safely, comments explaining why and stops — no partial PR is opened
8. Commits, pushes the branch, and opens a **draft PR** titled `[Claude] #N: {issue title}`

## Reviewing the result

- The issue will have a WIP comment once Claude picks it up
- A draft PR will appear in `nemerosa/ontrack` — review the diff and description
- Nothing is merged without explicit human approval — Claude only creates draft PRs

## Future automation

This workflow runs on demand in a local session. If you want it to run on a schedule or via an API call without an open session, it can be promoted to a [Claude Code Routine](https://claude.ai/code/routines) (requires a Pro/Max/Team/Enterprise plan).
