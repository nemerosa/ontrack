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
  }

}