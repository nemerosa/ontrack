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
import javax.sql.DataSource

@Repository
class AccountJdbcRepository(
        dataSource: DataSource,
        private val authenticationSourceRepository: AuthenticationSourceRepository
) : AbstractJdbcRepository(dataSource), AccountRepository {

    override fun findBuiltinAccount(username: String): BuiltinAccount? {
        return getFirstItem(
                "SELECT * FROM ACCOUNTS WHERE PROVIDER = :provider AND NAME = :name",
                params("name", username)
                        .addValue("provider", BuiltinAuthenticationSourceProvider.ID)
        ) { rs: ResultSet, _: Int ->
            toAccount(rs)?.let { account ->
                BuiltinAccount(
                        account,
                        rs.getString("password")
                )
            }
        }
    }

    private fun toAccount(rs: ResultSet): Account? {
        val authenticationSource = rs.getAuthenticationSource(authenticationSourceRepository)
        return authenticationSource?.let {
            of(
                    rs.getString("name"),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    getEnum(SecurityRole::class.java, rs, "role"),
                    authenticationSource,
                    disabled = rs.getBoolean("disabled"),
                    locked = rs.getBoolean("locked"),
            ).withId(id(rs))
        }
    }

    override fun findAll(): Collection<Account> {
        return jdbcTemplate!!.query(
                "SELECT * FROM ACCOUNTS ORDER BY NAME"
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }
    }

    override fun newAccount(account: Account): Account {
        return try {
            val id = dbCreate(
                    "INSERT INTO ACCOUNTS (NAME, FULLNAME, EMAIL, PROVIDER, SOURCE, PASSWORD, ROLE) " +
                            "VALUES (:name, :fullName, :email, :provider, :source, :password, :role)",
                    account.authenticationSource.asParams()
                            .addValue("name", account.name)
                            .addValue("fullName", account.fullName)
                            .addValue("email", account.email)
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

    override fun getAccount(accountId: ID): Account {
        return namedParameterJdbcTemplate!!.queryForObject(
                "SELECT * FROM ACCOUNTS WHERE ID = :id",
                params("id", accountId.value)
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        } ?: throw AccountNotFoundException(accountId.value)
    }

    override fun deleteAccountBySource(source: AuthenticationSource) {
        namedParameterJdbcTemplate!!.update(
                "DELETE FROM ACCOUNTS WHERE PROVIDER = :provider AND SOURCE = :source",
                source.asParams()
        )
    }

    override fun doesAccountIdExist(id: ID): Boolean {
        return getFirstItem(
                "SELECT ID FROM ACCOUNTS WHERE ID = :id",
                params("id", id.value),
                Int::class.java
        ) != null
    }

    override fun findByNameToken(token: String): List<Account> {
        return namedParameterJdbcTemplate!!.query(
                "SELECT * FROM ACCOUNTS WHERE LOWER(NAME) LIKE :filter ORDER BY NAME",
                params("filter", String.format("%%%s%%", StringUtils.lowerCase(token)))
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }
    }

    override fun getAccountsForGroup(accountGroup: AccountGroup): List<Account> {
        return namedParameterJdbcTemplate!!.query(
                "SELECT A.* FROM ACCOUNTS A " +
                        "INNER JOIN ACCOUNT_GROUP_LINK L ON L.ACCOUNT = A.ID " +
                        "WHERE L.ACCOUNTGROUP = :accountGroupId " +
                        "ORDER BY A.NAME ASC",
                params("accountGroupId", accountGroup.id())
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }
    }

    override fun findAccountByName(username: String): Account? {
        return getFirstItem(
                "SELECT * FROM ACCOUNTS WHERE NAME = :name",
                params("name", username)
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }
    }

    override fun setAccountDisabled(id: ID, disabled: Boolean) {
        namedParameterJdbcTemplate!!.update(
            "UPDATE ACCOUNTS SET DISABLED = :disabled WHERE ID = :id",
            params("id", id.get())
                .addValue("disabled", disabled)
        )
    }

    override fun setAccountLocked(id: ID, locked: Boolean) {
        namedParameterJdbcTemplate!!.update(
            "UPDATE ACCOUNTS SET LOCKED = :locked WHERE ID = :id",
            params("id", id.get())
                .addValue("locked", locked)
        )
    }
}