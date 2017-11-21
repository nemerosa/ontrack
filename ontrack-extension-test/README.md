## Testing locally

First, publish the Ontrack core modules locally:

```
./gradlew clean build publishToMavenLocal dockerBuild
```

Gets the version having been published by running:

```
./gradlew versionDisplay
```

and noting down the `full` version, for example `feature-552-kotlin-1160-1ab95db-dirty` in the output below:

```
[version] scm        = git
[version] branch     = feature/552-kotlin-1160
[version] branchType = feature
[version] branchId   = feature-552-kotlin-1160
[version] commit     = 1ab95db843fa4534daf1a24b2aac5e17bf04a5c8
[version] full       = feature-552-kotlin-1160-1ab95db-dirty
[version] base       = 552-kotlin-1160
[version] build      = 1ab95db
[version] display    = feature-552-kotlin-1160-1ab95db-dirty
[version] tag        = 
[version] dirty      = true
```

Then, go to the `ontrack-extension-test` folder and run:

```bash
./gradlew clean build -PontrackVersion=feature-552-kotlin-1160-1ab95db-dirty
```

replacing the version by your own of course.
