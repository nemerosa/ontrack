Enabling Security Remember Me feature
=====================================

* How to enable this feature with a LDAP authentication source?

See http://stackoverflow.com/questions/24745528/spring-security-ldap-and-remember-me#24853922

* How to test?

Login normally.
Delete the `JSESSIONID` cookie, and keep the `remember-be` one.
Close the page
Open the page

