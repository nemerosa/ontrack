package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.*;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorisations {

    public static Authorisations none() {
        return new Authorisations(
                null,
                new LinkedHashSet<>()
        );
    }

    @Getter(AccessLevel.PRIVATE)
    private GlobalRole globalRole;
    @Getter(AccessLevel.PRIVATE)
    private Set<ProjectRoleAssociation> projectRoleAssociations = new LinkedHashSet<>();

    public boolean isGranted(Class<? extends GlobalFunction> fn) {
        return (globalRole != null && globalRole.isGlobalFunctionGranted(fn));
    }

    public boolean isGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return (globalRole != null && globalRole.isProjectFunctionGranted(fn))
                || projectRoleAssociations.stream().anyMatch(pa -> pa.getProjectId() == projectId && pa.isGranted(fn));
    }

    public Authorisations withGlobalRole(Optional<GlobalRole> globalRole) {
        this.globalRole = globalRole.orElse(null);
        return this;
    }

    public Authorisations withProjectRoles(Collection<ProjectRoleAssociation> projectRoleAssociations) {
        this.projectRoleAssociations.addAll(projectRoleAssociations);
        return this;
    }

    public Authorisations withProjectRole(ProjectRoleAssociation projectRoleAssociation) {
        this.projectRoleAssociations.add(projectRoleAssociation);
        return this;
    }
}
