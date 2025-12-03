# GitHub CI engine (`github`)

The GitHub [CI engine](../../../configuration/ci-config.md#ci-engines) is the one used when you call Yontrack from GitHub Actions workflows.

## Detection

The GitHub CI engine is automatically detected when the `GITHUB_ACTIONS` environment variable is set to `true` (which is done by default in GitHub Actions workflows).

## Project name

The project name is, by order of priority:

* the value of the `PROJECT_NAME` environment variable if available
* the name of the repository in the `GITHUB_REPOSITORY` environment variable

## Build configuration

This engine will link the Yontrack build to the GitHub Actions workflow run.

## Environment variables

The following environment variables are passed to Yontrack:

* `GITHUB_ACTIONS`
* `GITHUB_SERVER_URL`
* `GITHUB_REPOSITORY`
* `GITHUB_REF_NAME`
* `GITHUB_RUN_ID`
* `GITHUB_RUN_NUMBER`
* `GITHUB_WORKFLOW`
* `GITHUB_EVENT_NAME`
* `GITHUB_SHA` 
* `VERSION`


!!! note

    No other environment variable is passed to prevent exposing secrets.

These environment variables are used for:

| Name                | Description                                                                       |
|---------------------|-----------------------------------------------------------------------------------|
| `GITHUB_ACTIONS`    | Used by Yontrack to detect that the call is coming from a GitHub Actions workflow |
| `GITHUB_SERVER_URL` | Used for linking to GitHub Actions workflow run                                   |
| `GITHUB_REPOSITORY` | For the SCM URL and the project name (repository part)                            |
| `GITHUB_REF_NAME`   | For the SCM branch name                                                           |
| `GITHUB_RUN_ID`     | Used for linking to GitHub Actions workflow run                                   |
| `GITHUB_RUN_NUMBER` | Used for linking to GitHub Actions workflow run and the build suffix              |
| `GITHUB_WORKFLOW`   | Used for linking to GitHub Actions workflow run                                   |
| `GITHUB_EVENT_NAME` | Used for linking to GitHub Actions workflow run                                   |
| `GITHUB_SHA`        | Git commit associated with the build                                              |
| `VERSION`           | Build label/release                                                               |
