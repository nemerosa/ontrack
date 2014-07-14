package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;
import java.util.Optional;

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
                Authorisations.none(),
                false
        );
    }

    private final ID id;
    private final String name;
    private final String fullName;
    private final String email;
    private final SecurityRole role;
    @Getter(AccessLevel.PRIVATE)
    private Authorisations authorisations;
    @Getter(AccessLevel.PRIVATE)
    private final boolean locked;

    public boolean isGranted(Class<? extends GlobalFunction> fn) {
        return (SecurityRole.ADMINISTRATOR == role)
                || authorisations.isGranted(fn);
    }

    public boolean isGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return SecurityRole.ADMINISTRATOR == role
                || authorisations.isGranted(projectId, fn);
    }

    public Account withId(ID id) {
        checkLock();
        return new Account(
                id,
                name,
                fullName,
                email,
                role,
                authorisations,
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
                authorisations,
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
        authorisations = authorisations.withGlobalRole(globalRole);
        return this;
    }

    public Account withProjectRoles(Collection<ProjectRoleAssociation> projectRoleAssociations) {
        checkLock();
        authorisations = authorisations.withProjectRoles(projectRoleAssociations);
        return this;
    }

    public Account withProjectRole(ProjectRoleAssociation projectRoleAssociation) {
        checkLock();
        authorisations = authorisations.withProjectRole(projectRoleAssociation);
        return this;
    }
}
