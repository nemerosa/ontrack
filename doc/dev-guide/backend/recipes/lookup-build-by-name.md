# Lookup of a Build using its display name or its name

Some services need to look for a `Build` using either its display name
(ie. the value attached to a build using a `ReleaseProperty`) or its name.

> Example: in the auto-versioning pull request generation, if we want to generate a change
> log in the PR's body, we need to look for the build in the source project using its "version",
> which may either be its display name or its name.

For this purpose, use the `BuildDisplayNameService`:

```kotlin
val buildDisplayNameService: BuildDisplayNameService

val build: Build? = buildDisplayNameService.findBuildByDisplayName(
    project,
    name,
    onlyDisplayName = false, // or true to use exclusively the display name
)
```
