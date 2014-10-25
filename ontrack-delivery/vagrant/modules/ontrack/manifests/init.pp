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

}