package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;

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
                (rs, num) -> AccountGroup.of(
                        rs.getString("name"),
                        rs.getString("description")
                ).withId(id(rs))
        );
    }
}
