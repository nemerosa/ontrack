# Emits is the third delivery map edge kind

The delivery map had two edge kinds, and `DeliveryMapEdgeKind` said so in as many words: the set is
closed, an edge which neither grants nor permits is not a dependency, and a third meaning would be a
change to what the map says rather than a new contributor. #1711 is that change. **Emits** joins
**unlocks** and **requires**, and this records why the closed set was reopened rather than bent.

## What emits means

Reaching the source sets the target off, and nothing waits for the result. It runs from the
checkpoint to its consequence — promotion level to workflow, slot to its `DONE` workflow — which is
the same direction every other edge runs in: from the thing that happens first to the thing that
follows.

Two things it deliberately is not:

* not **unlocks**, because the target is not a checkpoint the source grants. A workflow is not
  something a build reaches; it is something the promotion caused;
* not **requires**, because nothing is constrained. A promotion workflow subscribed to
  `NEW_PROMOTION_RUN` runs *after* the promotion is granted; a slot's `DONE` workflow runs after the
  deployment is over. Drawing either as a prerequisite would tell a reader that something might
  block, when nothing can — the same error ADR 0010 exists to prevent, in the other direction.

## Why the two existing kinds could not carry it

The alternative was drawing everything workflow-shaped as a **requires** and letting the reader work
out which ones actually gate. That is exactly the reading the two kinds were split to prevent: the
map's whole claim is that a line's kind says whether a configuration *acts* or merely *permits*, and
a third behaviour flattened into either one makes both less trustworthy.

The other alternative — not drawing the downstream workflows at all — loses the question the issue
exists to answer. A promotion whose canary verification failed is a fact about that promotion, and
the map is where the promotion is already being read.

## Why this is a smaller change than it looks

Half of what #1711 draws was already expressible. A slot workflow at `CANDIDATE` or `RUNNING` is a
**hard gate**: `SlotServiceImpl` refuses to start a deployment when a `CANDIDATE` check is not ok,
and refuses to finish one when a `RUNNING` check is not ok — unconditionally, with no admission rule
involved. Those are prerequisites, and they are drawn with the existing *requires*, workflow to slot,
exactly as an admission rule is. Only the downstream half — promotion to workflow, slot to `DONE`
workflow — needed a word the map did not have.

A slot carrying workflows on several triggers therefore straddles its own column: its gates to the
left, its consequences to the right.

## How it interacts with ADR 0010

`withoutShadowedRequires` needs no change and gets none. It drops a **requires** only where the same
directed pair also carries an **unlocks**, and an emits pair can collide with neither: the promotion
side runs from a promotion level to a workflow checkpoint, which nothing else ever names, and the
slot side runs slot → workflow while the gating edges run workflow → slot. Different pairs, opposite
directions.

This is stated here rather than left to be rediscovered, because the suppression rule is deliberately
general — any directed pair, any contributor — and "the new kind is out of its way" is a fact about
the pair of rules, not about either one.

## What it costs

**The closed set is now a set of three, and the argument for closing it is one round weaker.** That
is the real price, and it is why this document exists: the reason two words were worth defending is
that someone wrote down why. Without the argument on record, the fourth kind is free.

The bar for a fourth: a line meaning something that is neither a grant, nor a constraint, nor a
consequence — and an existing kind actively misleading rather than merely imprecise. A new
*contributor* needs none of this. Only a new *meaning* for a line does.

## What follows from it

Anyone adding an edge kind must add it to `edgeKinds` in `deliveryMapModel.js` with a **dash pattern
of its own** — never a colour. Colour is not what tells the kinds apart on this map, because a dash
survives greyscale and colour blindness; `unlocks` is solid, `requires` is `6 4`, `emits` is `1 4`.
`GQLEnumDeliveryMapEdgeKind` picks a new enum value up on its own and needs no change.
