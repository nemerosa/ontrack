# Generic CI engine (`generic`)

The generic [CI engine](../../../configuration/ci-config.md#ci-engines) is used for contexts where the CI engine is not [GitHub](github.md) or [Jenkins](jenkins.md).

## Detection

!!! warning

    The generic CI engine is never automatically detected. It must be explicitly declared, typically using the `ci` parameter of your [integration](../../../configuration/ci-config.md#integrations).

## Project name

The project name is the value of the `PROJECT_NAME` environment variable.

## Environment variables

These environment variables are used for:

| Name             | Description                      |
|------------------|----------------------------------|
| `PROJECT_NAME`   | Name of the project              |
| `BRANCH_NAME`    | Name of the branch               |
| `BUILD_NUMBER`   | Build name suffix                |
| `VERSION`        | Build label/release              |
| `SCM_URL`        | SCM URL                          |
| `BUILD_REVISION` | Commit or revision for the build |
