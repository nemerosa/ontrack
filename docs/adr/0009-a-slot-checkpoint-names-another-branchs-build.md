# A slot checkpoint on a branch's delivery map names another branch's build

The delivery map is a branch view, and every other checkpoint on it names a build **of that
branch**: a promotion level names the last build of the branch promoted there, a validation
stamp the last one run there. A slot does not. It names the most recently deployed build of
the slot, whatever branch that build belongs to.

This supersedes the part of `0007-deployments-are-not-a-branch-view-concern.md` which says a
card naming another branch's build "would be a genuine surprise rather than a detail". That
reasoning stands for the pipeline view's promotion band, whose contract is *one card per
promotion level, counting builds of this branch*; it does not survive contact with the
delivery map, whose contract is *what a build on this branch has to pass through on its way to
an environment*. Production is on that path, and the map has to draw it.

## Why not the per-branch reading

The per-branch reading was available. `SlotService.findPipelines` takes a `branchName` filter,
exposed as `Slot.pipelines(branchName:, done:)`, so "the last build of THIS branch deployed
here" is one query away. It was considered and rejected.

The map would then be uniform — every checkpoint of every kind naming a build of the branch —
and would tell the most damaging lie available to it. On a project where releases go out from
`main` and a maintenance branch is being read, the production checkpoint would be empty, and an
empty production checkpoint reads as *nothing is in production*. What is actually in production
is the single most valuable fact the slot half of the map carries, and a uniform reading is not
worth hiding it.

It also fails at the moment it matters most. Someone reading a maintenance branch's map is
usually asking "can I ship this?", and the answer routinely depends on what is in production
right now — which is precisely the build the per-branch reading would drop.

## What follows from it

The exception is **drawn**, not merely tolerated. `SlotCheckpointData.otherBranch` carries the
branch name whenever it differs from the branch of the map, and the checkpoint says so in words
beside the build. A build name alone gives no hint of where it came from, and an unmarked
foreign build would be the surprise ADR 0007 warned about.

Slots are the one kind of checkpoint drawn even when this branch can never reach them. A slot
whose admission rules exclude the branch outright is marked unreachable and names no build at
all — including the build actually deployed in it, which is deliberately withheld there. "You
cannot get there from here" is the whole answer, and a build sitting beside it would argue with
it. Omitting such a slot altogether was rejected for the same reason the empty checkpoint was:
someone would be left wondering why production is missing.

The ordering of the two halves of the map does not match, and is not made to. A slot's build is
the most recently *deployed* one, ordered by pipeline number within the slot; a promotion level's
and a validation stamp's is the *highest build*, ordered by build id. They usually agree, and
disagree after an older build is redeployed. Reconciling them would mean either ordering
deployments by build id — which stops answering "what is in there now", the only question a slot
is asked — or ordering promotions by time, which is a change to two checkpoint kinds for the
benefit of a third.

## Consequences

`CONTEXT.md` already carries the exception in the definition of *checkpoint*, written when the
concept was named. This document is why it is there.

A future "the map shows a build that is not on this branch" report is answered by this document,
not by a filter.
