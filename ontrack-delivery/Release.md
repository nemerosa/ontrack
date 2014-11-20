Ontrack release cycle
=====================

## Overview

The release cycle goes about:

1. developments are done on the `develop` and the `feature/*` branches
1. developments targeted for the next release must be merged into the `develop` branch
1. when release `R.X` is ready for stabilisation, create the `release/R.X` branch from the `develop` one
1. fix any remaining issue on the `release/R.X` branch
1. when ready to actually release, merge the `release/R.X` branch into the `master`, tag, build and publish.

Merge regularly the `release/*` branches into the `develop` branch.

## Pipelines

For all branches:

* packaging
* local acceptance tests (using a local Docker image)

For `release/*` branches:

* deploying in a acceptance server

For `master` branch:

* deploying in production (manual, optional)

## Acceptance deployment and tests

    TODO

## Release and publication

Merging the release into the master:

    git checkout master
    git merge --no-ff release/R.X

Tagging and building:

    git tag R.X
    ./gradlew clean release

Deploy in acceptance and run the acceptance tests (see above).

Publication of the release in GitHub:

* creating the release
* attaching the _ontrack-ui_ JAR file
* getting the change log from Ontrack @ Ontrack and attaching it to the release as description - we get the change log
since the last _Release_ promoted build on the _master_ branch

Optional deployment in production and running smoke tests:

    TODO
