# GitHub SCM engine (`github`)

The [SCM engine](../../../configuration/ci-config.md#scm-engines) is used to connect Yontrack projects to GitHub repositories.

## Detection

The [CI engine](../../../configuration/ci-config.md#scm-engines) provides a SCM URL and Yontrack tries to match it against the available GitHub configurations.

## Configuration

The project is attached to the GitHub configuration in Yontrack.

The branch is attached to the repository branch.

The build is linked to the [Git commit](../../../generated/properties/property-net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType.md).
