package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class AccountJdbcRepository extends AbstractJdbcRepository implements AccountRepository {

    private final Logger logger = LoggerFactory.getLogger(AccountRepository.class);

    @Autowired
    public AccountJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean checkPassword(int accountId, Predicate<String> check) {
        String encodedPassword = getFirstItem(
                "SELECT PASSWORD FROM ACCOUNTS WHERE MODE = 'password' AND ID = :id",
                params("id", accountId),
                String.class
        );
        return encodedPassword != null && check.test(encodedPassword);
    }

    @Override
    public Account findUserByNameAndMode(String username, String mode) {
        Account account = getFirstItem(
                "SELECT * FROM ACCOUNTS WHERE MODE = :mode AND NAME = :name",
                params("name", username).addValue("mode", mode),
                (rs, rowNum) -> Account.of(
                        rs.getString("name"),
                        getEnum(SecurityRole.class, rs, "role")
                ).withId(id(rs))
        );
        // The account does exist
        if (account != null) {
            // Fills its ACL
            return accountWithACL(account).lock();
        } else {
            return null;
        }
    }

    private Account accountWithACL(Account account) {
        // Global functions
        for (Class<? extends GlobalFunction> fn : getGlobalFunctions(account.getId())) {
            account = account.with(fn);
        }
        // Project functions
        for (ProjectFn projectFn : getProjectFunctions(account.getId())) {
            account = account.with(projectFn.getId(), projectFn.getFn());
        }
        // OK
        return account;
    }

    private Collection<ProjectFn> getProjectFunctions(ID accountId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM PROJECT_AUTHORIZATIONS WHERE ACCOUNT = :accountId ORDER BY PROJECT, FN",
                params("accountId", accountId.getValue()),
                new RowMapper<ProjectFn>() {
                    @Override
                    public ProjectFn mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String fnName = rs.getString("fn");
                        int projectId = rs.getInt("project");
                        try {
                            //noinspection unchecked
                            Class<? extends ProjectFunction> fn = (Class<? extends ProjectFunction>) Class.forName(fnName);
                            return new ProjectFn(projectId, fn);
                        } catch (ClassNotFoundException e) {
                            // TODO Logs the event for investigation
                            // Log
                            logger.warn("Cannot parse project function name {} for account {} and project {}", fnName, accountId, projectId);
                            // Null - won't be added
                            return null;
                        }
                    }
                }
        );
    }

    private Collection<Class<? extends GlobalFunction>> getGlobalFunctions(ID accountId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM GLOBAL_AUTHORIZATIONS WHERE ACCOUNT = :accountId ORDER BY FN",
                params("accountId", accountId.getValue()),
                (rs, rowNum) -> {
                    String fnName = rs.getString("fn");
                    try {
                        //noinspection unchecked
                        return (Class<? extends GlobalFunction>) Class.forName(fnName);
                    } catch (ClassNotFoundException e) {
                        // TODO Logs the event for investigation
                        // Log
                        logger.warn("Cannot parse global function name {} for account {}", fnName, accountId);
                        // Null - won't be added
                        return null;
                    }
                }
        ).stream().filter(fn -> fn != null).collect(Collectors.toList());
    }
}
