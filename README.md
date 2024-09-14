ontrack
=======

[![codecov](https://codecov.io/gh/nemerosa/ontrack/branch/develop/graph/badge.svg)](https://codecov.io/gh/nemerosa/ontrack)
[![Slack chat](https://img.shields.io/badge/slack-ontrack-brightgreen.svg?logo=slack)](https://ontrack-run.slack.com/)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/nemerosa/ontrack.svg)](http://isitmaintained.com/project/nemerosa/ontrack "Average time to resolve an issue")

# [Continuous delivery monitoring](https://nemerosa.github.io/ontrack).

* [Quick start](#quick-start)
* [Documentation](#documentation)
* [Contributions](#contributions)

Store all events which happen in your CI/CD environment: branches, builds,
validations, promotions, labels, commits. Display this information in
dashboards. Search for builds based on statuses, issues, commits, etc. Use
this information as a powerful tool to drive your pipelines into new
directions!

![Ontrack builds](doc/readme/ontrack-builds.png)

Track your changes using
[logs](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#changelogs)
between builds/releases:

![Ontrack Git Commits](doc/readme/ontrack-git-commits.png)

([Git](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#usage-git)
and
[Subversion](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#usage-subversion)
are supported)

Issue change logs are of course available:

![Ontrack GitHub Changelog](doc/readme/ontrack-github-changelog.png)

Ontrack can communicate with many tools:
[GitHub](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#usage-github),
[Bitbucket](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#usage-bitbucket),
JIRA, Jenkins, Artifactory.

And if this is not enough, you can add your own
[extensions](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#extending).

You can feed information into Ontrack using:

* a REST / GraphQL API
* a [CLI](https://github.com/nemerosa/ontrack-cli)
* a [Jenkins pipeline library](https://github.com/nemerosa/ontrack-jenkins-cli-pipeline/)
* a [GitHub action](https://github.com/nemerosa/ontrack-github-actions-cli-setup)

## Quick start

### On Kubernetes

You can install Ontrack using its [Helm chart](https://github.com/nemerosa/ontrack-chart):

```
helm repo add ontrack https://nemerosa.github.io/ontrack-chart
```

To install the `ontrack` chart:

```
helm install ontrack ontrack/ontrack
```

To uninstall the chart:

```
helm delete ontrack
```

This installs 4 services:

* Ontrack itself
* a Postgres 15 database
* an Elasticsearch 7 single node
* a RabbitMQ message broker

> To connect to Ontrack, enable the ingress or activate a port forward.

See https://github.com/nemerosa/ontrack-chart for more options.

### With Docker Compose

On a local machine, you can start Ontrack using Docker Compose:

```bash
curl -fsSLO https://raw.githubusercontent.com/nemerosa/ontrack/master/compose/docker-compose.yml
docker compose up -d
```

This sets up:

* a Postgres database
* an ElasticSearch (single node)
* a RabbitMQ message broker
* Ontrack running on port 8080

Go to http://localhost:8080 and start using Ontrack.

The initial administrator credentials are `admin` / `admin`.

## Documentation

Full documentation available in the
[Ontrack web site](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html) or as
[PDF](https://static.nemerosa.net/ontrack/release/latest/docs/index.pdf).

Check also the blog articles at https://nemerosa.ghost.io/

## Contributions

[Contributions](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#contributing) are welcome!
