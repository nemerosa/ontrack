Vagrant + Puppet
================

Perfect to setup a VM, configure it, and discard it afterwards. The Puppet scripts must be independent from 
Vagrant since they can be used as-is for the production-like deployment. If both use cases use the same
provisioning mechanism, we can guarantee that the acceptance tests validate also the production deployment.

## Java installation

There is no clean built-in JDK8 image for Vagrant, and setup of the JDK8 using Puppet, even with the official
images remains complex. Switching back to a pure `apt-get` installation.

## Java installation (bis)

Downloading and installation of the JDK8 is not cached and takes too much time for a local and discardable
installation.

## nginx installation

The official `nginx` puppet scripts seem to be in infancy stage and very unstable. Reverting also to a pure
`apt-get` installation and configuration.

This can prove to be difficult when dealing with certificates.

## Vagrant vs. Puppet

We want the Puppet scripts to be independent from Vagrant. For the moment, the Puppet scripts are in the 
`vagrant` folder but should be extracted in their own folder.

## Resources

* http://www.linux.com/news/software/applications/694157-setup-your-dev-environment-in-nothing-flat-with-puppet
* http://www.linux.com/learn/tutorials/696255-jumpstart-your-linux-development-environment-with-puppet-and-vagrant
* https://docs.vagrantup.com/v2/provisioning/puppet_apply.html

Spring Boot init.d:

* https://github.com/rburgst/spring-boot-initscript/tree/master
* http://thoughtfulsoftware.wordpress.com/2014/06/01/running-spring-boot-part-ii/ (more up to date)
