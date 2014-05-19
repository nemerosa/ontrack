package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
                "SELECT PASSWORD FROM ACCOUNTS WHERE MODE = 'PASSWORD' AND ID = :id",
                params("id", accountId),
                String.class
        );
        return encodedPassword != null && check.test(encodedPassword);
    }

    @Override
    public Account findUserByNameAndMode(String username, String mode) {
        return getFirstItem(
                "SELECT * FROM ACCOUNT WHERE MODE = ':mode' AND NAME = :name",
                params("name", username).addValue("mode", mode),
                (rs, rowNum) -> Account.of(
                        rs.getString("name"),
                        getEnum(SecurityRole.class, rs, "role")
                ).withId(id(rs))
        );
    }
}
