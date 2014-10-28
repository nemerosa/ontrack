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

### Vagrant + Puppet

Perfect to setup a VM, configure it, and discard it afterwards. The Puppet scripts must be independent from 
Vagrant since they can be used as-is for the production-like deployment. If both use cases use the same
provisioning mechanism, we can guarantee that the acceptance tests validate also the production deployment.

#### Java installation

There is no clean built-in JDK8 image for Vagrant, and setup of the JDK8 using Puppet, even with the official
images remains complex. Switching back to a pure `apt-get` installation.

#### Java installation (bis)

Downloading and installation of the JDK8 is not cached and takes too much time for a local and discardable
installation.

#### nginx installation

The official `nginx` puppet scripts seem to be in infancy stage and very unstable. Reverting also to a pure
`apt-get` installation and configuration.

This can prove to be difficult when dealing with certificates.

#### Vagrant vs. Puppet

We want the Puppet scripts to be independent from Vagrant. For the moment, the Puppet scripts are in the 
`vagrant` folder but should be extracted in their own folder.


## Open points

### Portable acceptance tests

The acceptance tests are easy to run at build time, because of their link to the sources, but they need
actually to run outside of any source context, when run against remote acceptance servers and production.

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
