package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.ProjectRoleAssociation;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
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
