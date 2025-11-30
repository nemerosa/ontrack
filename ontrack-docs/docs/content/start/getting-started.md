# Getting started

The recommended way to install Yontrack is to use its Helm chart.

> Please refer to the [Helm chart documentation](https://github.com/nemerosa/ontrack-chart) for more information.

By default, Yontrack is secured using a simple user store managed by Keycloak.

In most cases, you'll configure Yontrack to use your own identity provider:

* [OIDC](../security/oidc.md)
* [LDAP](../security/ldap.md)

Once done, you can start configuring [groups](../security/groups.md) and map them to the [groups of your identity provider](../security/group-mappings.md).
