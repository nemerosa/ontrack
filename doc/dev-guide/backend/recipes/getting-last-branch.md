# Getting the last branch for a project using semantic rules

Given a `Project`, we can get the ordered list of its branches, from the newest to the oldest, using
a semantic ordering using the `BranchOrderingService`:

```kotlin
val branchOrderingService: BranchOrderingService
val branches: List<Branch>

val ordering = branchOrderingService.getSemVerBranchOrdering(
    branchNamePolicy = BranchNamePolicy.NAME_ONLY,
)
val orderedBranches = branches.sortedWith(ordering)
```
