Ontrack release cycle
=====================

## Overview

The release cycle goes about:

1. developments are done on the `master` and the `feature/*` branches
1. developments targeted for the next release must be merged into the `master` branch
1. when release `R.X` is ready for stabilisation, create the `release/R.X` branch from the `master` one
1. fix any remaining issue on the `release/R.X` branch
1. when ready to actually release, publish and release from the `release/R.X` branch
1. keep the `release/R.X` branch for maintenance

Merge regularly the `release/*` branches into the `master` branch.

Always merge from the most stable release branch to the least one:

```
release/2.1
   \--> release/2.2
         \--> release/2.3
               \--> master
```

## Versioning

Version component   | `release/R`       | `other/abc`
--------------------| ------------------|-------
**Display version** | R                 | other-abc
Full version        | release-R-1234567 | other-abc-1234567
Branch              | release-R         | other-abc
Build               | 1234567           | 1234567
Source              | release/R         | other/abc
Source type         | release           | other
Commit              | 1234567...        | 1234567...


## Pipeline

* Packaging - all branches
* Local acceptance tests and Docker image build - all branches
* Docker image publication - auto. for `release/*` and `master` branches, optional for the other
* Digital Ocean acceptance deployment and tests - auto. for `release/*` and `master` branches, optional for the other
* Publication of release in GitHub and Ontrack - optional, for `release/*` branches only
* Deployment in production  - optional, for `release/*` branches only

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

## Publication

For a `release/R` branch:

* creating a release `R` in GitHub
* uploading the `ontrack-ui.jar`
* getting the change log from Ontrack @ Ontrack and setting it as description into the GitHub release
* setting the new `R` build as `RELEASED` in Ontrack @ Ontrack

Getting the change is an issue because we do not have one unique branch to take into account. We want to get a change log between the current build and the last `RELEASE` build, overall on the project.

**Ontrack does not allow yet** to get a change log between two builds on different branches. See issue [#172](https://github.com/nemerosa/ontrack/issues/172) for a status on this feature.

In the meantime, the change log has to be collected manually.

## Production deployment

See [[Operations]].

## Housekeeping

* unreleased images in Docker Hub
* unreleased images in Jenkins
* stopped containers at Ontrack @ Ontrack
* old acceptance droplets in Digital Ocean
