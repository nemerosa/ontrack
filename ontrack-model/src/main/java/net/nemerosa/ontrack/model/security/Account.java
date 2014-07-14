package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.*;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account implements Entity {

    public static Account of(String name, String fullName, String email, SecurityRole role) {
        return new Account(
                ID.NONE,
                name,
                fullName,
                email,
                role,
                null, // No global role
                new HashSet<>(), // No project role
                false
        );
    }

    private final ID id;
    private final String name;
    private final String fullName;
    private final String email;
    private final SecurityRole role;
    @Getter(AccessLevel.PRIVATE)
    private GlobalRole globalRole;
    @Getter(AccessLevel.PRIVATE)
    private Set<ProjectRoleAssociation> projectRoleAssociations = new LinkedHashSet<>();
    @Getter(AccessLevel.PRIVATE)
    private final boolean locked;


    public boolean isGranted(Class<? extends GlobalFunction> fn) {
        return (SecurityRole.ADMINISTRATOR == role)
                || (globalRole != null && globalRole.isGlobalFunctionGranted(fn));
    }

    public boolean isGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return SecurityRole.ADMINISTRATOR == role
                || (globalRole != null && globalRole.isProjectFunctionGranted(fn))
                || projectRoleAssociations.stream().anyMatch(pa -> pa.getProjectId() == projectId && pa.isGranted(fn));
    }

    public Account withId(ID id) {
        checkLock();
        return new Account(
                id,
                name,
                fullName,
                email,
                role,
                globalRole,
                projectRoleAssociations,
                locked
        );
    }

    public Account lock() {
        return new Account(
                id,
                name,
                fullName,
                email,
                role,
                globalRole,
                projectRoleAssociations,
                true
        );
    }

    private void checkLock() {
        if (locked) {
            throw new IllegalStateException("Account is locked");
        }
    }

    public Account withGlobalRole(Optional<GlobalRole> globalRole) {
        checkLock();
        this.globalRole = globalRole.orElse(null);
        return this;
    }

    public Account withProjectRoles(Collection<ProjectRoleAssociation> projectRoleAssociations) {
        checkLock();
        this.projectRoleAssociations.addAll(projectRoleAssociations);
        return this;
    }

    public Account withProjectRole(ProjectRoleAssociation projectRoleAssociation) {
        checkLock();
        this.projectRoleAssociations.add(projectRoleAssociation);
        return this;
    }
}
