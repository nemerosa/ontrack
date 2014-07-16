package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account implements Entity {

    public static Account of(String name, String fullName, String email, SecurityRole role, AuthenticationSource authenticationSource) {
        return new Account(
                ID.NONE,
                name,
                fullName,
                email,
                authenticationSource,
                role,
                new LinkedHashSet<>(),
                Authorisations.none(),
                false
        );
    }

    private final ID id;
    private final String name;
    private final String fullName;
    private final String email;
    private final AuthenticationSource authenticationSource;
    private final SecurityRole role;
    private final Collection<AccountGroup> accountGroups;
    @Getter(AccessLevel.PRIVATE)
    private Authorisations authorisations;
    @Getter(AccessLevel.PRIVATE)
    private final boolean locked;

    public boolean isGranted(Class<? extends GlobalFunction> fn) {
        return (SecurityRole.ADMINISTRATOR == role)
                || accountGroups.stream().anyMatch(group -> group.isGranted(fn))
                || authorisations.isGranted(fn);
    }

    public boolean isGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return SecurityRole.ADMINISTRATOR == role
                || accountGroups.stream().anyMatch(group -> group.isGranted(projectId, fn))
                || authorisations.isGranted(projectId, fn);
    }

    public Account withId(ID id) {
        checkLock();
        return new Account(
                id,
                name,
                fullName,
                email,
                authenticationSource,
                role,
                accountGroups,
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
                authenticationSource,
                role,
                accountGroups,
                authorisations,
                true
        );
    }

    public Account update(AccountInput input) {
        return new Account(
                id,
                input.getName(),
                input.getFullName(),
                input.getEmail(),
                authenticationSource,
                role,
                accountGroups,
                authorisations,
                locked
        );
    }

    private void checkLock() {
        if (locked) {
            throw new IllegalStateException("Account is locked");
        }
    }

    public Account withGroup(AccountGroup accountGroup) {
        checkLock();
        this.accountGroups.add(accountGroup);
        return this;
    }

    public Account withGroups(Collection<AccountGroup> groups) {
        checkLock();
        this.accountGroups.addAll(groups);
        return this;
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
