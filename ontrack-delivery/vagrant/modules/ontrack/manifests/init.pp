# Class ontrack
#
# Parameters:
#
# * ontrackJar - path on the guest to the Ontrack JAR to install
class ontrack($ontrackJar = undef) {

# Check
  if (!$ontrackJar) {
    fail('Path to the Ontrack JAR must be set ($ontrackJar).')
  }

# Ontrack user
  user { 'ontrack':
    ensure => present,
  }

# Depends on Java
  include java

# Ontrack directory
  file { '/opt/ontrack':
    ensure  => 'directory',
    group   => 'ontrack',
    owner   => 'ontrack',
    require => User['ontrack']
  }

# TODO Stores version in a file, gets it as a fact, in order to avoid doing the download each time
# Or gets the version in the downloaded file, and uses it as an output of the Exec task below
# Note that in this case, the start-up template must be adapted

# Ontrack application JAR
  file { 'ontrack-install':
    name      =>'/opt/ontrack/ontrack.jar',
    require   => File['/opt/ontrack'],
    ensure    => 'present',
    group     => 'ontrack',
    owner     => 'ontrack',
    source    => $ontrackJar,
  }

# init.d script for ontrack
  file { 'ontrack-init.d':
    require  => File['ontrack-install'],
    path     => '/etc/init.d/ontrack',
    ensure   => 'file',
    content  => template('ontrack/init.erb'),
    mode     => '0755',
  }

# ontrack service
  service { 'ontrack-service':
    name       => 'ontrack',
    require    => File['ontrack-init.d'],
    subscribe  => File['ontrack-install'],
    enable     => true,
    ensure     => running,
  }

}