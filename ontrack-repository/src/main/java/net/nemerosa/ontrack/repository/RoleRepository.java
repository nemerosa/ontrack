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

    Optional<ProjectRoleAssociation> findProjectRoleAssociationsByAccount(
            int accountId, int projectId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper);

    Ack saveGlobalRoleForAccount(int accountId, String role);

    Ack saveGlobalRoleForGroup(int accountGroupId, String role);

    /**
     * Gets the global role for an account group
     */
    Optional<String> findGlobalRoleByGroup(int groupId);

    Collection<ProjectRoleAssociation> findProjectRoleAssociationsByGroup(
            int groupId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper
    );

    Optional<ProjectRoleAssociation> findProjectRoleAssociationsByGroup(
            int groupId, int projectId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper
    );

    Ack deleteGlobalRoleForAccount(int accountId);

    Ack deleteGlobalRoleForGroup(int groupId);

    Ack saveProjectRoleForAccount(int projectId, int accountId, String role);

    Ack saveProjectRoleForGroup(int projectId, int accountGroupId, String role);

    Ack deleteProjectRoleForAccount(int projectId, int accountId);

    Ack deleteProjectRoleForGroup(int projectId, int accountGroupId);
}
