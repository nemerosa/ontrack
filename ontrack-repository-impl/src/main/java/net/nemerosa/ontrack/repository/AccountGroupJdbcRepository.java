package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.AccountGroupNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.exceptions.AccountGroupNotFoundException;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * TABLE ACCOUNT_GROUP_LINK (
 * ACCOUNT      INTEGER NOT NULL,
 * ACCOUNTGROUP INTEGER NOT NULL
 * );
 * </pre>
 */
@Repository
public class AccountGroupJdbcRepository extends AbstractJdbcRepository implements AccountGroupRepository {

    @Autowired
    public AccountGroupJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Collection<AccountGroup> findByAccount(int accountId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT G.* FROM ACCOUNT_GROUPS G " +
                        "INNER JOIN ACCOUNT_GROUP_LINK L ON L.ACCOUNTGROUP = G.ID " +
                        "WHERE L.ACCOUNT = :accountId",
                params("accountId", accountId),
                (rs, num) -> toAccountGroup(rs)
        );
    }

    @Override
    public void linkAccountToGroups(int accountId, Collection<Integer> groupIds) {
        // Removing existing links
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM ACCOUNT_GROUP_LINK WHERE ACCOUNT = :accountId",
                params("accountId", accountId)
        );
        // Adding the links
        if (groupIds != null) {
            for (int groupId : groupIds) {
                getNamedParameterJdbcTemplate().update(
                        "INSERT INTO ACCOUNT_GROUP_LINK (ACCOUNT, ACCOUNTGROUP) VALUES (:accountId, :groupId)",
                        params("accountId", accountId).addValue("groupId", groupId)
                );
            }
        }
    }

    @Override
    public List<AccountGroup> findByNameToken(String token) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM ACCOUNT_GROUPS WHERE LOWER(NAME) LIKE :filter ORDER BY NAME",
                params("filter", String.format("%%%s%%", StringUtils.lowerCase(token))),
                (rs, num) -> toAccountGroup(rs)
        );
    }

    private AccountGroup toAccountGroup(ResultSet rs) throws SQLException {
        return new AccountGroup(
                id(rs),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("autojoin")
        );
    }

    @Override
    public List<AccountGroup> findAll() {
        return getJdbcTemplate().query(
                "SELECT * FROM ACCOUNT_GROUPS ORDER BY NAME",
                (rs, num) -> toAccountGroup(rs)
        );
    }

    @Override
    public AccountGroup newAccountGroup(AccountGroup group) {
        try {
            return group.withId(
                    ID.of(
                            dbCreate(
                                    "INSERT INTO ACCOUNT_GROUPS (NAME, DESCRIPTION, AUTOJOIN) " +
                                            "VALUES (:name, :description, :autoJoin)",
                                    params("name", group.getName())
                                            .addValue("description", group.getDescription())
                                            .addValue("autoJoin", group.getAutoJoin())
                            )
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountGroupNameAlreadyDefinedException(group.getName());
        }
    }

    @Override
    public AccountGroup getById(ID groupId) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM ACCOUNT_GROUPS WHERE ID = :id",
                    params("id", groupId.getValue()),
                    (rs, num) -> toAccountGroup(rs)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new AccountGroupNotFoundException(groupId);
        }
    }

    @Override
    public void update(AccountGroup group) {
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ACCOUNT_GROUPS SET NAME = :name, DESCRIPTION = :description, AUTOJOIN = :autoJoin " +
                            "WHERE ID = :id",
                    params("name", group.getName())
                            .addValue("description", group.getDescription())
                            .addValue("autoJoin", group.getAutoJoin())
                            .addValue("id", group.id())
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountGroupNameAlreadyDefinedException(group.getName());
        }
    }

    @Override
    public Ack delete(ID groupId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM ACCOUNT_GROUPS WHERE ID = :id",
                        params("id", groupId.getValue())
                )
        );
    }
}
