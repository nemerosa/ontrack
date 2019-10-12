package net.nemerosa.ontrack.model.security;

public enum SecurityRole {

    ADMINISTRATOR("ROLE_ADMIN"),

    USER("ROLE_USER");

    /**
     * Security role name
     */
    private final String roleName;

    SecurityRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleAbbreviatedName() {
        return roleName.substring(5);
    }
}
