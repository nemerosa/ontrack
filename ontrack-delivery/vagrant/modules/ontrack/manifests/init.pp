class ontrack {

# Apt for tools
  include apt
  exec { 'apt-get-update':
    command => '/usr/bin/apt-get update',
  }

# Tools
  package { [
    "curl",
    "git-core",
    "bash"]:
    ensure  => present,
    require => Exec["apt-get-update"],
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

# Ontrack download
  exec {
    "ontrack-install":
      require   => File['/opt/ontrack'],
      user      => 'ontrack',
      cwd       => '/opt/ontrack',
      path      => "/usr/bin/:/bin/",
      logoutput => true,
    # TODO ontrack could be available through the Web, or locally, for acceptance tests
      command   => 'curl --location https://github.com/nemerosa/ontrack/releases/download/2.0.0-beta-8-74/ontrack.jar --output ontrack.jar',
    # TODO Wrong: this output does not take the version in account
      creates   => '/opt/ontrack/ontrack.jar',
  }

# Shell scripts for starting and stopping
  file { 'ontrack-startup':
    require  => Exec['ontrack-install'],
    path     => '/opt/ontrack/start.sh',
    ensure   => 'file',
    group    => 'ontrack',
    owner    => 'ontrack',
    mode     => 'ug=rx',
    content  => template('ontrack/start.erb'),
  }

  file { 'ontrack-stop':
    require  => Exec['ontrack-install'],
    path     => '/opt/ontrack/stop.sh',
    ensure   => 'file',
    group    => 'ontrack',
    owner    => 'ontrack',
    mode     => 'ug=rx',
    content  => template('ontrack/stop.erb'),
  }

}