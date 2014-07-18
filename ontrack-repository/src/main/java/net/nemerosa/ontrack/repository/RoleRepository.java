package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.ProjectRoleAssociation;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

public interface RoleRepository {

    /**
     * Gets the global role for an account
     */
    Optional<String> findGlobalRoleByAccount(int accountId);

    Collection<ProjectRoleAssociation> findProjectRoleAssociationsByAccount(
            int accountId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper
    );

    Ack saveGlobalRoleForAccount(int accountId, String role);

    Ack saveGlobalRoleForGroup(int accountId, String role);
}
