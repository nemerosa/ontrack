# Development quick start

To start developing with Ontrack, follow these instructions.

## Prerequisistes

You need:

* a JDK 17
* Docker
* `docker-compose`

## Getting the code

Get a clean working copy of the Ontrack GitHub repository:

```bash
git clone git@github.com:nemerosa/ontrack.git
cd ontrack
```

## Prepares the UI code

Generate the UI code in development mode:

```bash
./gradlew :ontrack-web:dev
```

## Prepares the environment

Start the middleware needed by Ontrack:

```bash
./gradlew devStart
```

This starts:

* a Postgres database
* an ElasticSearch single-node instance
* a RabbitMQ server

## Running Ontrack

### Using Gradle

```bash
./gradlew :ontrack-ui:bootRun
```

This starts Ontrack on http://localhost:8080.

The default credentials are:

* username: `admin`
* password: `admin`

### Using the IDE

* main class: `net.nemerosa.ontrack.boot.Application`
* Spring profile to activate: `dev`

## Developing the UI

When developing UI components, you can activate an automated refresh of the web resources.

For the "core" web resources under `ontrack-web`, you can launch:

```bash
./gradlew watch
```

This will refresh the application web resources on any change in the web files. The `dev` profile must be active.

For web resources in any of the extensions (in `ontrack-extension-*` directories), just rebuild the application (in your IDE for example) and refresh the web page.
