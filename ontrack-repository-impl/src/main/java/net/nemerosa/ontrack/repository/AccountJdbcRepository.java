package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.structure.ID;
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

    @Override
    public Account newAccount(Account account) {
        int id = dbCreate(
                "INSERT INTO ACCOUNTS (NAME, FULLNAME, EMAIL, MODE, PASSWORD, ROLE) " +
                        "VALUES (:name, :fullName, :email, :mode, :password, :role)",
                params("name", account.getName())
                        .addValue("fullName", account.getFullName())
                        .addValue("email", account.getEmail())
                        .addValue("mode", account.getAuthenticationSource().getId())
                        .addValue("password", "")
                        .addValue("role", account.getRole().name())
        );
        return account.withId(ID.of(id));
    }

    @Override
    public void setPassword(int accountId, String encodedPassword) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE ACCOUNTS SET PASSWORD = :password WHERE ID = :id",
                params("id", accountId)
                        .addValue("password", encodedPassword)
        );
    }

    @Override
    public Account getAccount(ID accountId, Function<String, AuthenticationSource> authenticationSourceFunction) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM ACCOUNTS WHERE ID = :id",
                params("id", accountId.getValue()),
                (rs, num) -> toAccount(
                        rs,
                        authenticationSourceFunction
                )
        );
    }
}
