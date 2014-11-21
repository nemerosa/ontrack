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

After the local acceptance tests for a `b` branch, a local Docker image has been created: `ontrack:b-ddd`.

This image must first be published on Docker Hub:

    docker tag ontrack:b-ddd nemerosa/ontrack:b-ddd
    docker login --email="" --username="" --password=""
    docker push nemerosa/ontrack:b-ddd
    docker logout

Then, using Vagrant @ Digital Ocean, we can provision the machine:

    ./vagrant-install.sh \
        --vagrant-provider=digital_ocean \
        --vagrant-host=ontrack-acceptance-b-ddd
        ...

and then run the acceptance tests against this machine:

    ./acceptance.sh --ontrack-url=https://... --acceptance=...

## Release and publication

Merging the release into the master:

    git checkout master
    git merge --no-ff release/R.X

Tagging and building:

    git tag R.X
    ./gradlew clean release

Local Docker setup and acceptance tests:

    ./local-docker-acceptance.sh

Pushing the Docker image(s):

    docker tag ontrack:R.x nemerosa/ontrack:R.x
    docker login --email="" --username="" --password=""
    docker push nemerosa/ontrack:R.x
    docker logout

Deploy in acceptance and run the acceptance tests (see above).

Publication of the release in GitHub:

* creating the release
* attaching the _ontrack-ui_ JAR file
* getting the change log from Ontrack @ Ontrack and attaching it to the release as description - we get the change log
since the last _Release_ promoted build on the _master_ branch

Optional deployment in production and running smoke tests:

    TODO

## Housekeeping

* unreleased images in Docker Hub
* unreleased images in Jenkins
