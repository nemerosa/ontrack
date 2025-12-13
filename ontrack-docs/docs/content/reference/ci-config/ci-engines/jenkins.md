# Jenkins CI engine (`jenkins`)

The Jenkins [CI engine](../../../configuration/ci-config.md#ci-engines) is the one used when you call Yontrack from Jenkins pipelines.

## Detection

The Jenkins CI engine is automatically detected when the `JENKINS_URL` environment variable is set.

## Project name

The project name is, by order of priority:

* the value of the `PROJECT_NAME` environment variable if available
* the name of the repository in any Git location (SSH or HTTPS) specified by the `GIT_URL` environment variable

## Build configuration

This engine will link the Yontrack build to the Jenkins build.

## Configuration detection

Yontrack will look for an existing Jenkins configuration based on the `JENKINS_URL` environment variable.

## Environment variables

The following environment variables are passed to Yontrack:

* starting with `GIT_`
* starting with `JOB_`
* starting with `NODE_`
* starting with `BUILD_`
* `JENKINS_URL`
* `BRANCH_NAME`
* `VERSION`
* `GIT_URL`

!!! note

    No other environment variable is passed to prevent exposing secrets.

These environment variables are used for:

| Name           | Description                                                                                         |
|----------------|-----------------------------------------------------------------------------------------------------|
| `JENKINS_URL`  | Used by Yontrack to detect that the call is coming from Jenkins.                                    |
| `GIT_URL`      | used by Yontrack to detect the Git URL and therefore the SCM type (Bitbucket, GitHub, GitLab, etc.) |           
| `GIT_COMMIT`   | used by Yontrack to get the commit being built                                                      |
| `JOB_NAME` an  | `BUILD_NUMBER` - used by Yontrack to create a link to the job                                       |           
| `BRANCH_NAME`  | used by Yontrack to detect the branch name                                                          |
| `VERSION`      | used by Yontrack to detect the version (label/release name)                                         |
| `BUILD_NUMBER` | used by Yontrack to create a suffix for the build technical name                                    |
