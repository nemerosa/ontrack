Automated deployment
====================

## Use cases

### Local acceptance tests

Launched using `localAcceptanceTests`. They just launch a local JVM with `ontrack` running in it, and accessed
 directly on http://localhost:9999. There is no need for a provisioned VM.
 
However, those tests do validate `ontrack` in a real situation, when deployed remotely, behind an `nginx` SSL proxy.

### Remote acceptance tests

We want to create a VM, deploy `ontrack` on it, run some tests and discard the VM. The created VM must be a real
one, with `ontrack` being deployed with the release artifact, and running behind an `nginx` SSL proxy.

### Production deployment

Same as for the _remote acceptance tests_, with the difference that the VM already exists. Additionally, a subset
of the acceptance tests can be run against the production server upon deployment.

### Standalone acceptance tests

The acceptance tests are easy to run at build time, because of their link to the sources, but they need
actually to run outside of any source context, when run against remote acceptance servers and production.

See [acceptance tests](../ontrack-acceptance/README.md) for more details.

## Technologies

* [Vagrant + Puppet](vagrant/README.md)
* [Docker](docker/README.md).

## Usage

Several scripts are provided with the `ontrack-delivery` module:

* for running the acceptance tests - `acceptance.sh`
* for running the acceptance tests against a Docker container - `docker-acceptance.sh`
* for running the acceptance tests against a Vagrant VM provisioned with Puppet - `vagrant-acceptance.sh`

### Acceptance tests alone

Usage, to test a production like machine:

    ./acceptance.sh \
        --jar=<path to Ontrack Acceptance JAR> \
        --ontrack-url=<ontrack URL> \
        --ontrack-context=production

This will execute the acceptance tests against the specified Ontrack application and output the JUnit XML report
in the local _ontrack-acceptance.xml_ file.

### Docker

The Docker client must be configured on the machine where the acceptance tests are run. In particular, the 
`DOCKER_HOST` environment variable must be correctly set.

Then, just run:

    ./docker-acceptance.sh \
        --jar=<path on Ontrack Application JAR> \
        --acceptance=<path to Ontrack Acceptance JAR> \
        --docker-user=`id -u <user>`

This will:

1. create an Ontrack container
1. make sure the local `acceptance` folder, which contains Ontrack data is accessible to the local `<user>` account
1. execute the acceptance tests against the Ontrack container
1. output the JUnit XML report in the local _ontrack-acceptance.xml_ file

### Vagrant

Use cases:
* remote acceptance tests

Usage:

    # Set-up the machine
    ./vagrant-setup.sh \
        --jar=<path to Ontrack JAR>
    # Launches integration tests
    ./acceptance.sh \
        --jar=<path to Ontrack Acceptance JAR> \
        --ontrack-url=http://localhost:3000
    # Getting rid of the VM (or not)
    cd vagrant-local
    vagrant destroy -f
