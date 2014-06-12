ontrack testing
===============

* _unit tests_ are always run. Unit tests should not access resources not load the application context (think: fast!)
* _integration tests_ are run using the `integration` profile. They can access resources or load the application context
* _acceptance tests_ are run using the `acceptance` profile and are normally hosted in the `ontrack-acceptance`
module

In order to run the _unit tests_ only:

    mvn test
    
In order to add the _integration tests_:

    mvn test -P integration
    
The acceptance tests need to run against a _deployed_ instance of _ontrack_. The `ontrack.url` system property or
`ONTRACK_URL` environment variable must be set to the base URL of the deployed application.

For example, to test against the application deployed at http://host

    mvn verify -P acceptance -Dontrack.url=http://host
    
Additionally, it is possible to launch automatically a local server on port 9999 by using the 
`acceptance-local` profile:
 
    mvn verify -P acceptance -P acceptance-local
