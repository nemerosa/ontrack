# Feeding Yontrack from Jenkins pipelines

!!! note

    Make sure [Yontrack](../configuration/jenkins.md) is configured for Jenkins.

## Yontrack token

First, your Jenkins pipelines need to be able to connect to Yontrack.

In Yontrack, [create a token](../../security/tokens.md).

In your Jenkins instance, create:

* a global variable named `YONTRACK_URL` containing the URL of your Yontrack instance
* a text secret named `YONTRACK_TOKEN` containing the token you created in the previous step

## Defining the Yontrack pipeline library in Jenkins

See the documentation at https://github.com/nemerosa/ontrack-jenkins-cli-pipeline to see how to register the pipeline
library in your Jenkins instance.

## Calling Yontrack pipeline steps

You're now ready to start feeding information into Yontrack from your Jenkins pipelines.

Start by creating a `.yontrack/ci.yaml` file at the root of your repository:

```yaml
version: v1
configuration: { }
```

!!! note

    This uses a default configuration for the items created in Yontrack.

    See [CI Configuration](../../configuration/ci-config.md) for more information.

In your `Jenkinsfile`, import the [
`ontrack-jenkins-cli-pipeline`](https://github.com/nemerosa/ontrack-jenkins-cli-pipeline) Jenkins pipeline library:

```groovy
@Library("ontrack-jenkins-cli-pipeline@v5") _
```

There are several main actions available in this library, but in short, you'll need to:

* [configure your project, branch & build in Yontrack](#ci-config)
* [perform some validations](#validations)

### CI Config

Call the CI Config as soon as possible in your pipeline:

```groovy
ontrackCliCIConfig()
```

This configures a project, branch and a build in Yontrack based on your settings in the `.yontrack/ci.yaml` file.

#### Release

If you want to associate a release label with the build, the `VERSION` environment variable must be set before you call
`ontrackCliCIConfig`.

If this environment variable cannot be set at this moment for any reason, you can still associate a release label later
using the `ontrackCliBuildRelease` step. If the `VERSION` environment variable is set, you can just call:

```groovy
ontrackCliBuildRelease()
```

or you can set it explicitly:

```groovy
ontrackCliBuildRelease(version: "my-version")
```

### Validations

To set a validation for a given stage, you'll typically call `ontrackCliValidate` in a post action:

```groovy
stage("...") {
    steps {
        // ...
    }
    post {
        always {
            ontrackCliValidate(stamp: "stamp-name")
        }
    }
}
```

!!! note

    The status of the validation is automatically computed from the result of the stage and
    some [run info](../../concepts/model/index.md#run-info) is associated with the validation.

If you use [typed validations](../../concepts/model/index.md#validation-stamp-types), you can use dedicated steps. For
example, to collect a test summary:

```groovy
stage("...") {
    steps {
        // ...
    }
    post {
        always {
            ontrackCliValidateTests(
                    stamp: 'BUILD',
                    pattern: '**/build/test-results/**/*.xml',
            )
        }
    }
}
```

!!! note

    More validation steps are available in the [Jenkins pipeline library](https://github.com/nemerosa/ontrack-jenkins-cli-pipeline).
