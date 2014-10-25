class ontrack {

  # Ontrack user
  user { 'ontrack':
    ensure => present,
  }

  # Depends on Java
  include java

}