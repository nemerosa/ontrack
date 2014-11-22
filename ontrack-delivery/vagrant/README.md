Vagrant set-up for Ontrack
==========================

The Vagrant set-up for Ontrack is based on Docker.

### Usage

To create the virtual machine:

    vagrant up

### Using Digital Ocean as a provider

The following instructions (see https://github.com/smdahlen/vagrant-digitalocean)
have to be executed once in order to enable Digital Ocean as a provider for Vagrant:

* install the Digital Ocean plug-in for Vagrant:

    vagrant plugin install vagrant-digitalocean

* create a Personal Access Token in Digital Ocean

When the machine has been created in DO, you can connect to it as root using:

    # Your token
    export DO_TOKEN=...
    vagrant ssh

The `DO_TOKEN` environment variable is used internally by the _Vagrantfile_  to specify the `provider.token` used
by the Digital Ocean provider.
