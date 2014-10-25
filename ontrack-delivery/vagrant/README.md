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

## Actions

* [x] Running an empty machine
* [x] Running puppet apply
* [x] Installing Java
* [x] Installing ontrack
* [ ] Script to start ontrack on boot
* [ ] Passing parameters to download ontrack
* [ ] Open port in Vagrant
* [ ] Installing nginx as proxy
* [ ] Configuring SSL
* [ ] Find a better way to include modules
* [ ] Extract installation of Java in a module
* [ ] Extract installation of ontrack in a module
* [ ] Write Wiki documentation
* [ ] Write blog about this
