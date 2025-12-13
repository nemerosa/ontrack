-- 60. Cleanup of all data linked to the LDAP & OIDC settings

DELETE
FROM STORAGE
WHERE STORE = 'net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider';

DELETE
FROM SETTINGS
WHERE CATEGORY = 'net.nemerosa.ontrack.extension.ldap.LDAPSettings';
