package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RoleJdbcRepository extends AbstractJdbcRepository implements RoleRepository {

    @Autowired
    public RoleJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<String> findGlobalRoleByAccount(int accountId) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT ROLE FROM GLOBAL_AUTHORIZATIONS WHERE ACCOUNT = :accountId",
                        params("accountId", accountId),
                        String.class
                )
        );
    }

    @Override
    public Optional<String> findGlobalRoleByGroup(int groupId) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT ROLE FROM GROUP_GLOBAL_AUTHORIZATIONS WHERE ACCOUNTGROUP = :groupId",
                        params("groupId", groupId),
                        String.class
                )
        );
    }

    @Override
    public Ack deleteGlobalRoleForAccount(int accountId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM GLOBAL_AUTHORIZATIONS WHERE ACCOUNT = :accountId",
                        params("accountId", accountId)
                )
        );
    }

    @Override
    public Ack deleteGlobalRoleForGroup(int groupId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM GROUP_GLOBAL_AUTHORIZATIONS WHERE ACCOUNTGROUP = :groupId",
                        params("groupId", groupId)
                )
        );
    }

    @Override
    public Ack saveProjectRoleForAccount(int projectId, int accountId, String role) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM PROJECT_AUTHORIZATIONS WHERE ACCOUNT = :accountId AND PROJECT = :projectId",
                params("accountId", accountId).addValue("projectId", projectId)
        );
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "INSERT INTO PROJECT_AUTHORIZATIONS (PROJECT, ACCOUNT, ROLE) VALUES (:projectId, :accountId, :role)",
                        params("accountId", accountId).addValue("role", role).addValue("projectId", projectId)
                )
        );
    }

    @Override
    public Ack saveProjectRoleForGroup(int projectId, int groupId, String role) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM GROUP_PROJECT_AUTHORIZATIONS WHERE ACCOUNTGROUP = :groupId AND PROJECT = :projectId",
                params("groupId", groupId).addValue("projectId", projectId)
        );
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "INSERT INTO GROUP_PROJECT_AUTHORIZATIONS (PROJECT, ACCOUNTGROUP, ROLE) VALUES (:projectId, :groupId, :role)",
                        params("groupId", groupId).addValue("role", role).addValue("projectId", projectId)
                )
        );
    }

    @Override
    public Ack deleteProjectRoleForAccount(int projectId, int accountId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM PROJECT_AUTHORIZATIONS WHERE ACCOUNT = :accountId AND PROJECT = :projectId",
                        params("accountId", accountId).addValue("projectId", projectId)
                )
        );
    }

    @Override
    public Ack deleteProjectRoleForGroup(int projectId, int accountGroupId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM GROUP_PROJECT_AUTHORIZATIONS WHERE ACCOUNTGROUP = :accountGroupId AND PROJECT = :projectId",
                        params("accountGroupId", accountGroupId).addValue("projectId", projectId)
                )
        );
    }

    @Override
    public Collection<AccountGroup> findAccountGroupsByGlobalRole(GlobalRole globalRole, Function<ID, AccountGroup> accountGroupLoader) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT G.* FROM ACCOUNT_GROUPS G " +
                        "INNER JOIN GROUP_GLOBAL_AUTHORIZATIONS A " +
                        "ON A.ACCOUNTGROUP = G.ID " +
                        "WHERE A.ROLE = :role " +
                        "ORDER BY G.NAME",
                params("role", globalRole.getId()),
                (rs, num) -> accountGroupLoader.apply(id(rs))
        );
    }

    @Override
    public Collection<Account> findAccountsByGlobalRole(GlobalRole globalRole, Function<ID, Account> accountLoader) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT G.* FROM ACCOUNTS G " +
                        "INNER JOIN GLOBAL_AUTHORIZATIONS A " +
                        "ON A.ACCOUNT = G.ID " +
                        "WHERE A.ROLE = :role " +
                        "ORDER BY G.EMAIL",
                params("role", globalRole.getId()),
                (rs, num) -> accountLoader.apply(id(rs))
        );
    }

    @Override
    public Collection<AccountGroup> findAccountGroupsByProjectRole(Project project, ProjectRole projectRole, Function<ID, AccountGroup> accountGroupLoader) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT G.* FROM ACCOUNT_GROUPS G " +
                        "INNER JOIN GROUP_PROJECT_AUTHORIZATIONS A " +
                        "ON A.ACCOUNTGROUP = G.ID " +
                        "WHERE A.ROLE = :role " +
                        "AND A.PROJECT = :project " +
                        "ORDER BY G.NAME",
                params("role", projectRole.getId()).addValue("project", project.id()),
                (rs, num) -> accountGroupLoader.apply(id(rs))
        );
    }

    @Override
    public Collection<Account> findAccountsByProjectRole(Project project, ProjectRole projectRole, Function<ID, Account> accountLoader) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT G.* FROM ACCOUNTS G " +
                        "INNER JOIN PROJECT_AUTHORIZATIONS A " +
                        "ON A.ACCOUNT = G.ID " +
                        "WHERE A.ROLE = :role " +
                        "AND A.PROJECT = :project " +
                        "ORDER BY G.EMAIL",
                params("role", projectRole.getId()).addValue("project", project.id()),
                (rs, num) -> accountLoader.apply(id(rs))
        );
    }

    @Override
    public Collection<ProjectRoleAssociation> findProjectRoleAssociationsByAccount(
            int accountId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper
    ) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT PROJECT, ROLE FROM PROJECT_AUTHORIZATIONS WHERE ACCOUNT = :accountId",
                params("accountId", accountId),
                (rs, rowNum) -> projectRoleAssociationMapper.apply(
                        rs.getInt("project"),
                        rs.getString("role")
                )
        ).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProjectRoleAssociation> findProjectRoleAssociationsByAccount(
            int accountId, int projectId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper) {
        Optional<ProjectRoleAssociation> value = getFirstItem(
                "SELECT PROJECT, ROLE FROM PROJECT_AUTHORIZATIONS WHERE ACCOUNT = :accountId AND PROJECT = :projectId",
                params("accountId", accountId).addValue("projectId", projectId),
                (rs, rowNum) -> projectRoleAssociationMapper.apply(
                        rs.getInt("project"),
                        rs.getString("role")
                )
        );
        return value != null ? value : Optional.empty();
    }

    @Override
    public Collection<ProjectRoleAssociation> findProjectRoleAssociationsByGroup(
            int groupId,
            BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper
    ) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT PROJECT, ROLE FROM GROUP_PROJECT_AUTHORIZATIONS WHERE ACCOUNTGROUP = :groupId",
                params("groupId", groupId),
                (rs, rowNum) -> projectRoleAssociationMapper.apply(
                        rs.getInt("project"),
                        rs.getString("role")
                )
        ).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProjectRoleAssociation> findProjectRoleAssociationsByGroup(int groupId, int projectId, BiFunction<Integer, String, Optional<ProjectRoleAssociation>> projectRoleAssociationMapper) {
        Optional<ProjectRoleAssociation> firstItem = getFirstItem(
                "SELECT PROJECT, ROLE FROM GROUP_PROJECT_AUTHORIZATIONS WHERE ACCOUNTGROUP = :groupId AND PROJECT = :projectId",
                params("groupId", groupId).addValue("projectId", projectId),
                (rs, rowNum) -> projectRoleAssociationMapper.apply(
                        rs.getInt("project"),
                        rs.getString("role")
                )
        );
        return firstItem != null ? firstItem : Optional.empty();
    }

    @Override
    public Ack saveGlobalRoleForAccount(int accountId, String role) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM GLOBAL_AUTHORIZATIONS WHERE ACCOUNT = :accountId",
                params("accountId", accountId)
        );
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "INSERT INTO GLOBAL_AUTHORIZATIONS (ACCOUNT, ROLE) VALUES (:accountId, :role)",
                        params("accountId", accountId).addValue("role", role)
                )
        );
    }

    @Override
    public Ack saveGlobalRoleForGroup(int groupId, String role) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM GROUP_GLOBAL_AUTHORIZATIONS WHERE ACCOUNTGROUP = :groupId",
                params("groupId", groupId)
        );
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "INSERT INTO GROUP_GLOBAL_AUTHORIZATIONS (ACCOUNTGROUP, ROLE) VALUES (:groupId, :role)",
                        params("groupId", groupId).addValue("role", role)
                )
        );
    }
}
