package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.AccountNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
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

    @Nullable
    @Override
    public BuiltinAccount findBuiltinAccount(@NotNull String username) {
        return getFirstItem(
                "SELECT * FROM ACCOUNTS WHERE MODE = 'password' AND NAME = :name",
                params("name", username),
                (rs, rowNum) -> new BuiltinAccount(
                        rs.getInt("ID"),
                        rs.getString("name"),
                        rs.getString("fullName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        getEnum(SecurityRole.class, rs, "role")
                )
        );
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
        try {
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
        } catch (DuplicateKeyException ex) {
            throw new AccountNameAlreadyDefinedException(account.getName());
        }
    }

    @Override
    public void saveAccount(Account account) {
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ACCOUNTS SET NAME = :name, FULLNAME = :fullName, EMAIL = :email " +
                            "WHERE ID = :id",
                    params("id", account.id())
                            .addValue("name", account.getName())
                            .addValue("fullName", account.getFullName())
                            .addValue("email", account.getEmail())
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountNameAlreadyDefinedException(account.getName());
        }
    }

    @Override
    public Ack deleteAccount(ID accountId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM ACCOUNTS WHERE ID = :id",
                        params("id", accountId.getValue())
                )
        );
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

    @Override
    public List<Account> findByNameToken(String token, Function<String, AuthenticationSource> authenticationSourceFunction) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM ACCOUNTS WHERE LOWER(NAME) LIKE :filter ORDER BY NAME",
                params("filter", String.format("%%%s%%", StringUtils.lowerCase(token))),
                (rs, num) -> toAccount(
                        rs,
                        authenticationSourceFunction
                )
        );
    }

    @Override
    public List<Account> getAccountsForGroup(AccountGroup accountGroup, Function<String, AuthenticationSource> authenticationSourceFunction) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT A.* FROM ACCOUNTS A " +
                        "INNER JOIN ACCOUNT_GROUP_LINK L ON L.ACCOUNT = A.ID " +
                        "WHERE L.ACCOUNTGROUP = :accountGroupId " +
                        "ORDER BY A.NAME ASC",
                params("accountGroupId", accountGroup.id()),
                (rs, num) -> toAccount(
                        rs,
                        authenticationSourceFunction
                )
        );
    }
}
