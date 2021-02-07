package net.nemerosa.ontrack.dsl.v4

import groovy.transform.Canonical
import net.nemerosa.ontrack.dsl.v4.doc.DSL

@Canonical
@DSL("LDAP settings parameters.")
class LDAPSettings {
    boolean enabled = false
    String url
    String searchBase
    String searchFilter
    String user
    String password
    String fullNameAttribute
    String emailAttribute
    String groupAttribute
    String groupFilter
    String groupNameAttribute
    String groupSearchBase
    String groupSearchFilter
}
