package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.AccountGroupNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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

    private AccountGroup toAccountGroup(ResultSet rs) throws SQLException {
        return AccountGroup.of(
                rs.getString("name"),
                rs.getString("description")
        ).withId(id(rs));
    }

    @Override
    public List<AccountGroup> findAll() {
        return getJdbcTemplate().query(
                "SELECT * FORM ACCOUNT_GROUPS ORDER BY NAME",
                (rs, num) -> toAccountGroup(rs)
        );
    }

    @Override
    public AccountGroup newAccountGroup(AccountGroup group) {
        try {
            return group.withId(
                    ID.of(
                            dbCreate(
                                    "INSERT INTO ACCOUNT_GROUPS (NAME, DESCRIPTION) " +
                                            "VALUES (:name, :description)",
                                    params("name", group.getName())
                                            .addValue("description", group.getDescription())
                            )
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountGroupNameAlreadyDefinedException(group.getName());
        }
    }

    @Override
    public AccountGroup getById(ID groupId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM ACCOUNT_GROUPS WHERE ID = :id",
                params("id", groupId.getValue()),
                (rs, num) -> toAccountGroup(rs)
        );
    }

    @Override
    public void update(AccountGroup group) {
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ACCOUNT_GROUPS SET NAME = :name, DESCRIPTION = :description " +
                            "WHERE ID = :id",
                    params("name", group.getName())
                            .addValue("description", group.getDescription())
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
