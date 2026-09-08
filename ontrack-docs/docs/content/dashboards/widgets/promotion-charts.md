# Promotion charts

Four widgets display delivery metrics related to promotion levels. They all share the same configuration shape.

| Key | Description |
|-----|-------------|
| `home/PromotionLeadTimeChart` | Average time from build creation to promotion. |
| `home/PromotionFrequencyChart` | How often builds reach the promotion level. |
| `home/PromotionStabilityChart` | Percentage of builds that reach the promotion level. |
| `home/PromotionTTRChart` | Time-to-recovery: how quickly the promotion level is restored after a regression. |

## Configuration

| Field | Type | Description |
|-------|------|-------------|
| `project` | string | Project name. |
| `branch` | string | Branch name. |
| `promotionLevel` | string | Promotion level name. |
| `interval` | string | Bucket size for the chart (e.g. `"1d"`, `"1w"`). |
| `period` | string | Time window to display (e.g. `"1M"`, `"3m"`). |

## When the promotion level is missing

The widget resolves the promotion level by name every time it is displayed. If the configured
project, branch or promotion level does not exist any more, the widget title still names the
configuration, marked as `(not found)`, and the widget body says what is missing instead of a
chart. Edit the dashboard and reconfigure the widget to point it at an existing promotion level.

## Example

```yaml
- key: "home/PromotionLeadTimeChart"
  layout: {x: 0, y: 0, w: 6, h: 30}
  config:
    project: "Backend"
    branch: "main"
    promotionLevel: "PRODUCTION"
    interval: "1d"
    period: "1M"
```
