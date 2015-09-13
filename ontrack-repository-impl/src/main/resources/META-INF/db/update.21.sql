-- 21. Refactoring of LDAP settings (#320)

UPDATE SETTINGS SET CATEGORY = 'net.nemerosa.ontrack.extension.ldap.LDAPSettings' WHERE CATEGORY = 'net.nemerosa.ontrack.model.settings.LDAPSettings';
