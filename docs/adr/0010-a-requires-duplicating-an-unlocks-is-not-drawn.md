# A requires that duplicates an unlocks is not drawn

The delivery map has two edge kinds. An **unlocks** says that reaching the source grants the target
by itself, as auto promotion does; a **requires** says the target cannot be reached until the source
has been. Where the same directed pair carries both, `DeliveryMapServiceImpl` keeps the unlocks and
drops the requires.

The rule is **general**: any directed pair, whichever contributor produced either edge, and whatever
kind of checkpoint sits at each end. It is applied in `withoutShadowedRequires`, beside the existing
`.filter { … }.distinctBy { it.id }`, after every contributor has run — no contributor can see enough
of the map to apply it itself.

## Why the suppressed edge is provably dead

Auto promotion does not bypass the promotion path. It creates its promotion run through
`StructureServiceImpl.newPromotionRun` like a manual promotion or an API call, so every
`PromotionRunCheckExtension` runs on it, the previous-promotion condition included. And it passes,
every time, because the prerequisite the constraint names is the very level whose arrival triggered
the auto promotion.

So the suppressed requires is not merely redundant with the unlocks — it is a constraint that cannot
fire. Drawing both would put two lines with opposite readings on one pair and tell the reader that
something *might* block, when nothing can. The map would be saying less than the truth by drawing
more.

## Why it is not scoped to the previous-promotion condition

The rule arrived with #1710, which resolves the previous-promotion condition through a cascade and so
routinely produces a requires on a pair auto promotion already covers. Scoping the suppression to
that source was the obvious narrow move, and it is wrong.

An explicit `PromotionDependenciesProperty` naming a pair that auto-promotes is in exactly the same
position: the dependency is checked, always passes, and can never block anything. It is dead
configuration. Keeping its line while dropping the identical one from the condition would make the
map's answer depend on where the constraint was written rather than on what it does — two rules
where one is true, and a reader unable to predict either.

## What this costs

**The map deletes user-configured data from the view.** A promotion dependency someone deliberately
set is not drawn, with nothing on the map saying so. That is a real cost and it is accepted: the map
is a reading of the configuration, not an inventory of it, and its subject is what constrains
reaching a checkpoint. A constraint that can never constrain is not part of that subject.

It also means the map is **not** the place to notice dead configuration. Surfacing configuration that
points at nothing is #1705's subject, and dead-but-valid configuration would need its own treatment
if it is ever wanted; the suppression here is deliberately silent rather than half-drawn.

## What follows from it

Anyone adding a `DeliveryMapContributor` that emits `REQUIRES` edges needs to know this rule exists:
an edge a contributor emits may not reach the screen, and its own test will not show that, because
the rule lives in `DeliveryMapServiceImpl` and is tested there. A contributor test asserting "the
edge is contributed" stays true either way — which is the point of testing the rule once, centrally,
rather than in each contributor.

The rule is one-directional. `A unlocks B` suppresses `A requires B`; it says nothing about
`B requires A`, which is a different claim about a different pair and is drawn as usual.
