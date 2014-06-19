ontrack testing
===============

* _unit tests_ are always run. Unit tests should not access resources not load the application context (think: fast!)
* _integration tests_ are run using the `integrationTest` task. They can access resources or load the application context
* _acceptance tests_ are run using the `acceptanceTest` task and are hosted in the `ontrack-acceptance`
module

In order to run the _unit tests_ only:

    ./gradlew test
    
In order to add the _integration tests_:

    ./gradlew test integrationTest
    
The acceptance tests need to run against a _deployed_ instance of _ontrack_. The `ontrack.url` system property or
`ONTRACK_URL` environment variable must be set to the base URL of the deployed application.

For example, to test against the application deployed at http://host

    ./gradlew acceptanceTest -Dontrack.url=http://host
    
Additionally, it is possible to launch automatically a local server on port 9999 by using the 
`localAcceptanceTest` task:
 
    ./gradlew localAcceptanceTest

This task will:

1. get the `ontrack-ui` executable JAR
1. run the `ontrack-ui` executable JAR with the `acceptance` Spring profile
1. run the acceptance tests on this local server, at URL http://localhost:9999 (see below)
1. shutdown the local server

The port can be configured using the `ontrackPort` Gradle property (for example: `-PontrackPort=8081`).
