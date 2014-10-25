include ontrack

#include apt
#
#apt::ppa { "ppa:webupd8team/java": }
#
#exec { 'apt-get update':
#  command => '/usr/bin/apt-get update',
#  before  => Apt::Ppa["ppa:webupd8team/java"],
#}
#
#exec { 'apt-get update 2':
#  command => '/usr/bin/apt-get update',
#  require => [ Apt::Ppa["ppa:webupd8team/java"] ],
#}
#
#package { [
#  "curl",
#  "git-core",
#  "bash"]:
#  ensure  => present,
#  require => Exec["apt-get update"],
#  before  => Apt::Ppa["ppa:webupd8team/java"],
#}
#
#package { ["oracle-java8-installer"]:
#  ensure  => present,
#  require => Exec["apt-get update 2"],
#}
#
#exec {
#  "accept_license":
#    command   => "echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections && echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections",
#    cwd       => "/home/vagrant",
#    user      => "vagrant",
#    path      => "/usr/bin/:/bin/",
#    before    => Package["oracle-java8-installer"],
#    logoutput => true,
#}

## Installation ontrack
## TODO Extract in a module

#file { '/opt/ontrack':
#  ensure  => 'directory',
#  group   => 'ontrack',
#  owner   => 'ontrack',
#  require => User['ontrack']
#}
#
#
#exec {
#  "ontrack-install":
#    require   => File['/opt/ontrack'],
#    user      => 'ontrack',
#    cwd       => '/opt/ontrack',
#    path      => "/usr/bin/:/bin/",
#    logoutput => true,
#  # TODO ontrack could be available through the Web, or locally, for acceptance tests
#    command   => 'curl --location https://github.com/nemerosa/ontrack/releases/download/2.0.0-beta-8-74/ontrack.jar --output ontrack.jar',
#}
