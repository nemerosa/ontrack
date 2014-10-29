class java::aptsetup {

  include apt

  apt::ppa { "ppa:webupd8team/java": }

  exec { 'apt-get-java-update':
    command => '/usr/bin/apt-get update',
    before  => Exec['apt-get-java'],
  }

  exec { 'apt-get-java':
    command => '/usr/bin/apt-get update',
    require => [ Apt::Ppa["ppa:webupd8team/java"] ],
  }

}