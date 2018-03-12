Extension test module
=====================

This module is a valid Ontrack extension, used to test the extension
mechanism itself.

### Local test

While this extension is tested by Ontrack pipeline at build time, it is 
also important to be able to test this extension locally.

#### From the IDE

Make sure you have a Postgres database running. You can use, for example:

```bash
./gradlew devInit
```

To launch the extension:

```bash
cd ontrack-extension-test
./gradlew ontrackRun
```

#### Integration tests from the IDE

#### Integration tests on command line

In order to reproduce what happens in the Ontrack pipeline, follow this procedure.

Start by building the extension:

```bash
# Version to test
export ONTRACK_VERSION=3.34.3
# Building and testing the extension locally
cd ontrack-extension-test
./gradlew \
    -PontrackVersion=${ONTRACK_VERSION} \
    clean \
    build
```

This will build a Docker image with name: `nemerosa/ontrack-extension-test:${ONTRACK_VERSION}`.

Then, launch the test environment, which comprises:

* the Ontrack application itself
* a Postgres database as backend

```bash
cd ontrack-acceptance/src/main/compose
docker-compose --file docker-compose-ext.yml up -d ontrack postgresql selenium
```

To launch the tests:

```bash
cd ontrack-acceptance/src/main/compose
docker-compose --file docker-compose-ext.yml up ontrack_acceptance
```

The test results will be available in `ontrack-acceptance/src/main/compose/build`.

When done, all services can be stopped and destroyed using:

```bash
cd ontrack-acceptance/src/main/compose
docker-compose --file docker-compose-ext.yml down
```
