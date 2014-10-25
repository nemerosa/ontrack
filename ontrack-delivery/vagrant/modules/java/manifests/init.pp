class java {

  include java::aptsetup

  package { ["oracle-java8-installer"]:
    ensure  => present,
    require => Exec['apt-get-java'],
  }

  exec {
    "accept_license":
      command   => "echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections && echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections",
      cwd       => "/home/vagrant",
      user      => "vagrant",
      path      => "/usr/bin/:/bin/",
      before    => Package["oracle-java8-installer"],
      logoutput => true,
  }

}