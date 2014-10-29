## Prerequisites

Installing Vagrant.

	vagrant box add hashicorp/precise64
	# for VM Fusion
	
## Running

The very first time the machine is created:

    vagrant up

To rerun the provisioning:

    vagrant provision

## Resources

* http://www.linux.com/news/software/applications/694157-setup-your-dev-environment-in-nothing-flat-with-puppet
* http://www.linux.com/learn/tutorials/696255-jumpstart-your-linux-development-environment-with-puppet-and-vagrant
* https://docs.vagrantup.com/v2/provisioning/puppet_apply.html

Spring Boot init.d:

* https://github.com/rburgst/spring-boot-initscript/tree/master
* http://thoughtfulsoftware.wordpress.com/2014/06/01/running-spring-boot-part-ii/ (more up to date)

## Actions

* [x] Running an empty machine
* [x] Running puppet apply
* [x] Installing Java
* [x] Installing ontrack
* [x] Script to start ontrack on boot
* [ ] Passing parameters to download ontrack
* [ ] Open port in Vagrant
* [ ] Installing nginx as proxy
* [ ] Configuring SSL
* [ ] Find a better way to include modules
* [ ] `/opt/ontrack` as home directory for the `ontrack` user
* [x] Extract installation of Java in a module
* [x] Extract installation of ontrack in a module
* [ ] Write Wiki documentation
* [ ] Write blog about this
