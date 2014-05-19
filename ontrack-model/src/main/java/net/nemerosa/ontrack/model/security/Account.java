package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.HashSet;
import java.util.Set;

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
                new HashSet<>(),
                new HashSet<>(),
                false
        );
    }

    private final ID id;
    private final String name;
    private final String fullName;
    private final String email;
    private final SecurityRole role;
    @Getter(AccessLevel.PRIVATE)
    private final Set<Class<? extends GlobalFunction>> globalFunctions;
    @Getter(AccessLevel.PRIVATE)
    private final Set<ProjectFn> projectFns;
    @Getter(AccessLevel.PRIVATE)
    private final boolean locked;


    public boolean isGranted(Class<? extends GlobalFunction> fn) {
        return (SecurityRole.ADMINISTRATOR == role)
                || (globalFunctions.contains(fn));
    }

    public boolean isGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return SecurityRole.ADMINISTRATOR == role
                || projectFns.stream().anyMatch(acl -> acl.getId() == projectId && fn.isAssignableFrom(acl.getFn()));
    }

    public Account with(Class<? extends GlobalFunction> fn) {
        unlocked();
        globalFunctions.add(fn);
        return this;
    }

    public Account with(int projectId, Class<? extends ProjectFunction> fn) {
        unlocked();
        projectFns.add(new ProjectFn(projectId, fn));
        return this;
    }

    public Account withId(ID id) {
        unlocked();
        return new Account(
                id,
                name,
                fullName,
                email,
                role,
                globalFunctions,
                projectFns,
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
                globalFunctions,
                projectFns,
                true
        );
    }

    private void unlocked() {
        if (locked) {
            throw new IllegalStateException("Account is locked");
        }
    }


}
