package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;

import java.util.Collection;
import java.util.Optional;

/**
 * Group of accounts.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountGroup implements Entity {

    private final ID id;
    private final String name;
    private final String description;
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
                description,
                authorisations,
                true
        );
    }

    public static AccountGroup of(String name, String description) {
        return new AccountGroup(
                ID.NONE,
                name,
                description,
                Authorisations.none(),
                false);

    }

    public AccountGroup withId(ID id) {
        checkLock();
        return new AccountGroup(
                id,
                name,
                description,
                authorisations,
                locked
        );
    }

    public AccountGroup update(NameDescription input) {
        checkLock();
        return new AccountGroup(
                id,
                input.getName(),
                input.getDescription(),
                authorisations,
                locked
        );
    }
}
