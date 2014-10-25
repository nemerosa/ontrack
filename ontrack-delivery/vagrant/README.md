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
* [ ] Installing ontrack
* [ ] Open port in Vagrant
* [ ] Installing nginx as proxy
* [ ] Configuring SSL
* [ ] Write Wiki documentation
* [ ] Write blog about this
