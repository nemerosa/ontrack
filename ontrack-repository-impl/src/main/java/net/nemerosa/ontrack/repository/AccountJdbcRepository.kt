package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountNameAlreadyDefinedException
import net.nemerosa.ontrack.model.exceptions.AccountNotFoundException
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.security.Account.Companion.of
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

@Repository
class AccountJdbcRepository(dataSource: DataSource?) : AbstractJdbcRepository(dataSource), AccountRepository {

    override fun findBuiltinAccount(username: String): BuiltinAccount? {
        return getFirstItem(
                "SELECT * FROM ACCOUNTS WHERE MODE = 'password' AND NAME = :name",
                params("name", username)
        ) { rs: ResultSet, _: Int ->
            BuiltinAccount(
                    rs.getInt("ID"),
                    rs.getString("name"),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("password"),
                    getEnum(SecurityRole::class.java, rs, "role")
            )
        }
    }

    override fun findUserByNameAndSource(username: String, sourceProvider: AuthenticationSourceProvider): Optional<Account> {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM ACCOUNTS WHERE MODE = :mode AND NAME = :name",
                        params("name", username).addValue("mode", sourceProvider.source.id)
                ) { rs: ResultSet, _ -> toAccount(rs) { sourceProvider.source } }
        )
    }

    private fun toAccount(rs: ResultSet, authenticationSourceFunction: (String) -> AuthenticationSource): Account {
        return of(
                rs.getString("name"),
                rs.getString("fullName"),
                rs.getString("email"),
                getEnum(SecurityRole::class.java, rs, "role"),
                authenticationSourceFunction(rs.getString("mode"))
        ).withId(id(rs))
    }

    override fun findAll(authenticationSourceFunction: (String) -> AuthenticationSource): Collection<Account> {
        return jdbcTemplate!!.query(
                "SELECT * FROM ACCOUNTS ORDER BY NAME"
        ) { rs: ResultSet, _ ->
            toAccount(
                    rs,
                    authenticationSourceFunction
            )
        }
    }

    override fun newAccount(account: Account): Account {
        return try {
            val id = dbCreate(
                    "INSERT INTO ACCOUNTS (NAME, FULLNAME, EMAIL, MODE, PASSWORD, ROLE) " +
                            "VALUES (:name, :fullName, :email, :mode, :password, :role)",
                    params("name", account.name)
                            .addValue("fullName", account.fullName)
                            .addValue("email", account.email)
                            .addValue("mode", account.authenticationSource.id)
                            .addValue("password", "")
                            .addValue("role", account.role.name)
            )
            account.withId(of(id))
        } catch (ex: DuplicateKeyException) {
            throw AccountNameAlreadyDefinedException(account.name)
        }
    }

    override fun saveAccount(account: Account) {
        try {
            namedParameterJdbcTemplate!!.update(
                    "UPDATE ACCOUNTS SET NAME = :name, FULLNAME = :fullName, EMAIL = :email " +
                            "WHERE ID = :id",
                    params("id", account.id())
                            .addValue("name", account.name)
                            .addValue("fullName", account.fullName)
                            .addValue("email", account.email)
            )
        } catch (ex: DuplicateKeyException) {
            throw AccountNameAlreadyDefinedException(account.name)
        }
    }

    override fun deleteAccount(accountId: ID): Ack {
        return Ack.one(
                namedParameterJdbcTemplate!!.update(
                        "DELETE FROM ACCOUNTS WHERE ID = :id",
                        params("id", accountId.value)
                )
        )
    }

    override fun setPassword(accountId: Int, encodedPassword: String) {
        namedParameterJdbcTemplate!!.update(
                "UPDATE ACCOUNTS SET PASSWORD = :password WHERE ID = :id",
                params("id", accountId)
                        .addValue("password", encodedPassword)
        )
    }

    override fun getAccount(accountId: ID, authenticationSourceFunction: (String) -> AuthenticationSource): Account {
        return namedParameterJdbcTemplate!!.queryForObject(
                "SELECT * FROM ACCOUNTS WHERE ID = :id",
                params("id", accountId.value)
        ) { rs: ResultSet, _ ->
            toAccount(
                    rs,
                    authenticationSourceFunction
            )
        } ?: throw AccountNotFoundException(accountId.value)
    }

    override fun findByNameToken(token: String, authenticationSourceFunction: (String) -> AuthenticationSource): List<Account> {
        return namedParameterJdbcTemplate!!.query(
                "SELECT * FROM ACCOUNTS WHERE LOWER(NAME) LIKE :filter ORDER BY NAME",
                params("filter", String.format("%%%s%%", StringUtils.lowerCase(token)))
        ) { rs: ResultSet, _ ->
            toAccount(
                    rs,
                    authenticationSourceFunction
            )
        }
    }

    override fun getAccountsForGroup(accountGroup: AccountGroup, authenticationSourceFunction: (String) -> AuthenticationSource): List<Account> {
        return namedParameterJdbcTemplate!!.query(
                "SELECT A.* FROM ACCOUNTS A " +
                        "INNER JOIN ACCOUNT_GROUP_LINK L ON L.ACCOUNT = A.ID " +
                        "WHERE L.ACCOUNTGROUP = :accountGroupId " +
                        "ORDER BY A.NAME ASC",
                params("accountGroupId", accountGroup.id())
        ) { rs: ResultSet, _ ->
            toAccount(
                    rs,
                    authenticationSourceFunction
            )
        }
    }
}