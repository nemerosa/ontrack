package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Repository
public class AccountJdbcRepository extends AbstractJdbcRepository implements AccountRepository {

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
    public Optional<Account> findUserByNameAndSource(String username, AuthenticationSourceProvider sourceProvider) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM ACCOUNTS WHERE MODE = :mode AND NAME = :name",
                        params("name", username).addValue("mode", sourceProvider.getSource().getId()),
                        (rs, rowNum) -> toAccount(rs, mode -> sourceProvider.getSource())
                )
        );
    }

    private Account toAccount(ResultSet rs, Function<String, AuthenticationSource> authenticationSourceFunction) throws SQLException {
        return Account.of(
                rs.getString("name"),
                rs.getString("fullName"),
                rs.getString("email"),
                getEnum(SecurityRole.class, rs, "role"),
                authenticationSourceFunction.apply(rs.getString("mode"))
        ).withId(id(rs));
    }

    @Override
    public Collection<Account> findAll(Function<String, AuthenticationSource> authenticationSourceFunction) {
        return getJdbcTemplate().query(
                "SELECT * FROM ACCOUNTS ORDER BY NAME",
                (rs, num) -> toAccount(
                        rs,
                        authenticationSourceFunction
                )
        );
    }
}
