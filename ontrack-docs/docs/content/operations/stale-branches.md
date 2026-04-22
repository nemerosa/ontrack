# Stale branch management

Yontrack runs a daily cleanup job per project that can automatically disable or delete
branches. Two independent mechanisms are available and can be combined on the same project.

## Inactivity-based disabling

The **Stale branches** project property disables (and optionally deletes) branches that have
had no build activity for a configurable number of days.

| Field | Description |
|---|---|
| `disablingDuration` | Days of inactivity after which a branch is disabled (required) |
| `deletingDuration` | Days after disabling after which the branch is deleted (0 = never) |
| `includes` | Regex — only branches matching this pattern are considered |
| `excludes` | Regex — branches matching this pattern are excluded even if `includes` matches |
| `promotionsToKeep` | List of promotion level names — branches with at least one build carrying one of these promotions are never disabled |

See the [full property reference](../generated/properties/property-net.nemerosa.ontrack.extension.stale.StalePropertyType.md).

## Pattern-based auto-disabling

The **Auto-disabling of branches based on patterns** project property lets you define a
prioritised list of pattern items. Each branch is matched against the items in order; the
first matching item's mode is applied.

### Pattern item fields

| Field | Description |
|---|---|
| `includes` | List of regexes — branch name must match at least one (empty = match all) |
| `excludes` | List of regexes — branch name must not match any |
| `mode` | What to do with matching branches (see below) |
| `keepLast` | Number of branches to exempt from disabling, ordered by semantic version (0 = no exemption) |

### Modes

| Mode | Effect |
|---|---|
| **Keep** | Matching branches are always kept; the cleanup job does not touch them |
| **Disable** | Matching branches are disabled. If `keepLast > 0`, the most-recent N branches (by semver order) are exempted |
| **Keep last** | The last N branches (by semver order) are kept; all other matching branches are disabled |

!!! note "Keep last and the Disable mode"
    The `keepLast` field applies to **both** `Disable` and `Keep last` modes.
    Setting `keepLast = 0` with `Disable` disables every matching branch with no
    exceptions.

!!! note "Semantic version ordering"
    Branches are sorted by the version number embedded in their name (e.g. `release/1.2.3`
    → `1.2.3`). For names with only a trailing integer — such as `v1`, `v2`, …, `v21` —
    Yontrack falls back to ordering by that integer, so `v21` is considered the most
    recent.

### Example

Given branches `v1`, `v2`, …, `v20`, `v21` and the following policy:

| includes | mode | keepLast |
|---|---|---|
| `v.*` | Disable | 1 |

`v21` (the highest version) is kept enabled; `v1` through `v20` are disabled.

See the [full property reference](../generated/properties/property-net.nemerosa.ontrack.extension.stale.AutoDisablingBranchPatternsPropertyType.md).

### Configuring via CI config

The auto-disabling property can also be set from your `.yontrack/ci.yaml` file using the `auto-disabling` project extension. This is useful when you want the policy to be defined centrally in your repository alongside your pipeline configuration.

**Apply to all branches (under `defaults`):**

```yaml
version: v1
configuration:
  defaults:
    project:
      auto-disabling:
        patterns:
          - includes:
              - 'v.*'
            mode: DISABLE
            keepLast: 1
```

**Apply only when triggered from a specific branch (under `custom`):**

```yaml
version: v1
configuration:
  custom:
    configs:
      - conditions:
          - name: branch
            config: main
        project:
          auto-disabling:
            patterns:
              - includes:
                  - 'v.*'
                mode: DISABLE
                keepLast: 1
```

In the second example, the property is set on the project only when the CI run is on the `main` branch. This is the recommended approach so that the policy is updated from a single authoritative branch.

See the [CI config reference](../configuration/ci-config.md) for more details on `defaults` vs `custom` configurations and available conditions.

## Combining both mechanisms

The two checks are evaluated independently for each branch. A `Keep` verdict from either
mechanism always wins: if the inactivity check would disable a branch but the pattern-based
check keeps it (or vice-versa), the branch is left enabled.
