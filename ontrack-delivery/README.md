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

## Technologies

* [Vagrant + Puppet](vagrant/README.md)
* [Docker](docker/README.md).

## Open points

### Portable acceptance tests

The acceptance tests are easy to run at build time, because of their link to the sources, but they need
actually to run outside of any source context, when run against remote acceptance servers and production.

See [acceptance tests](../ontrack-acceptance/README.md) for more details.

## Usage

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
