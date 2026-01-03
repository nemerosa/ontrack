# Development quick start

To start developing with Yontrack, follow these instructions.

## Prerequisites

You need:

* JDK 21
* Docker (Desktop)

## Getting the code

Get a clean working copy of the Yntrack GitHub repository:

```bash
git clone git@github.com:nemerosa/ontrack.git yontrack
cd yontrack
```

## Prepares the environment

Start the middleware needed by Yontrack:

```bash
./gradlew devComposeUp
```

This starts:

* a Postgres database
* an ElasticSearch single-node instance
* a RabbitMQ server
* a Postgres database for Keycloak
* a Keycloak server
* a Kibana server

> To tear down the environment, you can run `./gradlew devComposeDown`.

## Running Ontrack

Launch the backend using:

Launch the frontend using:

This launches Yontrack on http://localhost:3000.

### Using the IDE

* main class: `net.nemerosa.ontrack.boot.Application`
* Spring profile to activate: `dev`
