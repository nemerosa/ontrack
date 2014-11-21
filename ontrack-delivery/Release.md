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

For `release/*` and `master` branches:

* Docker publication
* deploying in a acceptance server (automated for those branches, manual for the others)

For `release/*` branches:

* release phase - preparing the `master` branch

For `master` branch:

* publication - GitHub
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

The _Acceptance @ Digital Ocean_ step in the _Acceptance_ phase is automated for the `release/*` and `master` branches and manual for the other types of branches.

## Release and publication

### Preparing the master

Merging the release into the master:

    git checkout master
    git merge --no-ff release/R.X

Tagging and pushing:

    git tag R.X
    git push origin master
    git push origin R.X

This is done during the optional _Release_ phase of the pipeline for _any_ branch, which follows the _Acceptance @ Digital Ocean_ phase. This is included into the `prepare-release.sh` script.

### Master pipeline

When done, this triggers a pipeline for the `master` branch, using `R.X` as version since built from a tag.

In the end, we have:

* a tested image in Docker Hub: `nemerosa/ontrack:R.x`
* a Digital Ocean acceptance machine: `ontrack-acceptance-R.X`

### Publishing the release

The next steps are:

* releasing in GitHub:
  * creating a release `R.X`
  * uploading the `ontrack-ui.jar`
  * getting the change log from Ontrack @ Ontrack and setting it as description into the GitHub release
  * setting the new `R.X` build as `RELEASED` in Ontrack @ Ontrack
* deploying in production:
  * updating the Docker container at Ontrack @ Ontrack
    * stopping the old one
    * running a new container by pulling `nemerosa/ontrack:R.x`
    * setting the `R.X` build as `PRODUCTION` in Ontrack @ Ontrack

## Housekeeping

* unreleased images in Docker Hub
* unreleased images in Jenkins
* stopped containers at Ontrack @ Ontrack
