# Jenkins auto-versioning post-processing

You can delegate the post-processing to a Jenkins job.

There is a global configuration and there are a specific configuration at branch level (in the
`postProcessingConfig` [configuration parameter](auto-versioning.md#configuration)).

For the global configuration, you can go to _Settings > Jenkins Auto Versioning Processing_ and define the following
attributes:

* _Configuration_ - default Jenkins configuration to use for the connection
* _Job_ - default path to the job to launch for the post-processing, relative to the Jenkins root URL (note that `/job/`
  separators can be omitted)
* _Retries_ - the amount of times we check for successful scheduling and completion of the post-processing job
* _Retry interval_ - the time (in seconds) between two checks for successful scheduling and completion of the
  post-processing job

## Auto-versioning configuration

The `postProcessingConfig` property at branch level must contain the following parameters:

| Parameter       | Default value | Description                                                    |
|-----------------|---------------|----------------------------------------------------------------|
| `dockerImage`   | _Required_    | Docker image defining the environment                          |
| `dockerCommand` | _Required_    | Command to run in the working copy inside the Docker container |
| `commitMessage` | _Optional_    | Commit message for the post processed files                    |
| `config`        | _Optional_    | Jenkins configuration to use for the connection                |
| `job`           | _Optional_    | Path to the job to launch for the post processing              |
| `credentials`   | _Optional_    | List of credentials to inject in the command (see below)       |

Example of such a configuration:

```yaml
postProcessing: jenkins
postProcessingConfig:
  dockerImage: openjdk:11
  dockerCommand: ./gradlew dependencies --write-locks
  commitMessage: "Resolving the dependency locks"
```

## Jenkins job definition

The Jenkins job must accept the following parameters:

| Parameter        | Description                                                            |
|------------------|------------------------------------------------------------------------|
| `REPOSITORY_URI` | Git URI of the repository to upgrade                                   |
| `DOCKER_IMAGE`   | This image defines the environment for the command to run in.          |
| `COMMIT_MESSAGE` | Commit message to use to commit and push the upgrade.                  |
| `UPGRADE_BRANCH` | Branch containing the code to upgrade.                                 |
| `CREDENTIALS`    | Pipe (\|) separated list of credential entries to pass to the command. |
| `VERSION`        | The version which is upgraded to                                       |

The Jenkins job is responsible to:

* running a Docker container based on the `DOCKER_IMAGE` image
* inject any credentials defined by `CREDENTIALS` parameter
* checkout the `UPGRADE_BRANCH` branch of the repository at `REPOSITORY_URI` inside the container
* run the `DOCKER_COMMAND` command inside the container
* commit and push any change using the `COMMIT_MESSAGE` message to the `UPGRADE_BRANCH` branch
