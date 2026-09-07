# Patch releases

Once `main` has moved to 5.4, a defect in 5.3 is fixed on a `release/5.3` branch and published as
5.3.2. This page is the whole procedure.

The version machinery needed nothing: `VersionCalculator.computeReleaseVersion` derives the version
from the branch name — `release/5.3` scans `git tag -l` for `5.3.<n>` and answers `5.3.2` — and
`ci.yml` appends `-rc-<run>` on `release/*` exactly as it does on `main`. What was main-only was the
*release path*, and #1702 is what made it work from a release branch.

## The rules

These were settled in a design review. The rationale matters as much as the choice.

| # | Rule | Why |
|---|------|-----|
| 1 | **Patch the previous minor only**, until the next minor ships. One live release branch at a time. | Bounds the work: no N-branch fan-out whose `.yontrack/ci.yaml` drifts from main's. |
| 2 | **`SILVER` means "green" on a release branch**, not "verified on the demo". | Keeps `GOLD` meaningful — a human still decides — without a manual `DEMO.SMOKE` stamp asserting something that never happened. |
| 3 | **Release branches are cut lazily**, from the released tag, when a defect appears. | An always-on release branch pays a full CI run per push for a branch that may ship nothing. |
| 4 | **Fixes land on `main` first, then are cherry-picked** to the release branch. | A fix living only on the patch branch is a regression in the next minor waiting to happen. |
| 5 | **A patch must not contain a Flyway migration.** | If 5.3.2 adds `V82` and `main` independently adds `V82`, a user upgrading 5.3.2 → 5.4.0 hits a checksum conflict. If a fix needs a schema change, it is not a patch. |
| 6 | **Retire the release branch at the next minor cutover** — delete the git branch, keep the Yontrack branch. | The Yontrack branch holds the build and validation history for what shipped; deleting it destroys the audit trail. See [Minor cutover](minor-cutover.md). |

## Two names for one branch

Both appear below, and using the wrong one is the likeliest way to get this subtly wrong.

| | Name | Where it is used |
|---|------|------------------|
| **SCM branch** | `release/5.3` | `git`, the `branch` condition in `.yontrack/ci.yaml` (which matches `GITHUB_REF_NAME`), `ci.yml` |
| **Yontrack branch** | `release-5.3` | The Yontrack UI, `release.yml`'s `branch` input, `yontrack build search --branch` |

Yontrack escapes the name: `NameDescription` allows only `A-Za-z0-9._-`, so the slash becomes a dash.
The `GOLD` workflow passes `${branch}`, which renders the **Yontrack** name — which is exactly what
`release.yml` needs.

## The procedure

### 1. Fix on `main` first

Land the fix on `main` through the normal workflow. Rule 4: a fix that exists only on the patch
branch is a regression in 5.4 waiting to happen.

### 2. Create the release branch if it does not exist

```bash
git fetch --tags
git checkout -b release/5.3 5.3.1
```

Cut from the **released tag**, not from a commit on `main`: the point of a patch is that it is 5.3.1
plus one fix and nothing else.

Then take `main`'s `.yontrack/ci.yaml` if the branch predates #1702. A branch cut from an older tag
carries a config that knows nothing about patches — `SILVER` still requires `DEMO.SMOKE`, which no
patch can ever produce — and the release will be blocked exactly as it was before:

```bash
git checkout main -- .yontrack/ci.yaml
git commit -m "#<issue> Take main's CI configuration"
```

### 3. Cherry-pick the fix

```bash
git cherry-pick <sha from main>
```

No Flyway migrations (rule 5). If the fix needs one, it is not a patch.

### 4. Push, and let CI run

```bash
git push -u origin release/5.3
```

CI computes `5.3.2-rc-<run>` from the branch name and the tags, pushes the durable GHCR tags,
archives the docs artefact, and grants `BRONZE`. `SILVER` then auto-promotes on `BRONZE` alone, and
announces itself in `#notifications` with the release-branch message: *"… is green on `release-5.3`.
Verify the fix, then decide on GOLD."*

A freshly cut release branch produces a `-rc-1` build with **no fix in it** — the branch is the tag
until the cherry-pick lands. It reaches `SILVER` and announces itself all the same. That is expected,
not a bug: leave it, and grant `GOLD` on the build that carries the fix.

### 5. Write the release notes

Same as any release, and **before** `GOLD`: `rel_wiki` hard-fails on a missing `Release-5.3.2.md` or
a missing `](Release-5.3.2)` link in `Home.md`, before anything is published.

### 6. Grant `GOLD`

The `GOLD` workflow dispatches `release.yml` with `version: 5.3.2-rc-<run>` **and
`branch: release-5.3`**. Without that second input the build would be looked up on `main` and the
release would fail with `No build named 5.3.2-rc-<run> in yontrack/main`.

Two things then differ from a release off `main`:

* **The GitHub release is not marked Latest.** `resolve` compares 5.3.2 against every released tag
  and passes `--latest=false`, so 5.4.0 keeps the badge and the README's shields.io version does not
  flip back to the previous minor.
* **The changelog comes from git.** There is no previously `RELEASE`-promoted build on the branch —
  5.3.1 was released from a build on `main` — so `--from-promotion RELEASE` returns nothing and the
  body falls back to `git log 5.3.1..<sha>`. For a patch that range *is* the changelog: it is the
  cherry-picks.

Everything else is the ordinary release: four validations, `RELEASE` as the receipt, the
`#internal-releases` message and the `doc.yontrack.com` dispatch.

## Why `SILVER` is declared the way it is

`.yontrack/ci.yaml` declares `SILVER` as `BRONZE` alone, and a `custom.configs` block conditioned on
`^main$` adds `DEMO.SMOKE` back. That looks backwards, and it is deliberate.

`PromotionLevelConfiguration.merge` is **additive only** —
`validations = (validations + other.validations).distinct()` — so a `custom.configs` block can *add*
a validation to a promotion but can never remove one. "Declare `DEMO.SMOKE` in the defaults and
override it away on `release/*`" is not expressible. The only way round it is to invert: the defaults
declare the weakest form, and each branch kind adds what it can actually satisfy.

Deploying a patch to the demo is not the alternative — it would roll the demo back off 5.4 — and
stamping `DEMO.SMOKE` by hand would assert a verification that never happened. So `SILVER` means
"green" on a release branch, and `GOLD` stays the human gate either way.

`CoreConfigurationServiceIT.The demo verification is added to SILVER on main and not on a release
branch` is what fails when someone tidies the `^main$` block back into the defaults.

Notifications behave differently, and that is what makes the message swap possible:
`NotificationsCIConfigExtension` merges them **by name with override**, so a same-named `On SILVER`
in the `^release/.*$` block *replaces* the default's rather than adding a second subscription.

## See also

* [Releasing](release.md) — the release train on `main`
* [Minor cutover](minor-cutover.md) — retiring a release branch when the next minor ships
* [ADR 0006](../../docs/adr/0006-build-identity-through-the-release.md) — why the released build is an rc build
