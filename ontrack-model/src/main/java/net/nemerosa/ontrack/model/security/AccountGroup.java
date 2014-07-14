package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;
import java.util.Optional;

/**
 * Group of accounts.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountGroup implements Entity {

    private final ID id;
    private final String name;
    @Getter(AccessLevel.PRIVATE)
    private Authorisations authorisations;
    @Getter(AccessLevel.PRIVATE)
    private final boolean locked;

    public boolean isGranted(Class<? extends GlobalFunction> fn) {
        return authorisations.isGranted(fn);
    }

    public boolean isGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return authorisations.isGranted(projectId, fn);
    }

    private void checkLock() {
        if (locked) {
            throw new IllegalStateException("Account is locked");
        }
    }

    public AccountGroup withGlobalRole(Optional<GlobalRole> globalRole) {
        checkLock();
        authorisations = authorisations.withGlobalRole(globalRole);
        return this;
    }

    public AccountGroup withProjectRoles(Collection<ProjectRoleAssociation> projectRoleAssociations) {
        checkLock();
        authorisations = authorisations.withProjectRoles(projectRoleAssociations);
        return this;
    }

    public AccountGroup withProjectRole(ProjectRoleAssociation projectRoleAssociation) {
        checkLock();
        authorisations = authorisations.withProjectRole(projectRoleAssociation);
        return this;
    }

    public AccountGroup lock() {
        return new AccountGroup(
                id,
                name,
                authorisations,
                true
        );
    }

}
