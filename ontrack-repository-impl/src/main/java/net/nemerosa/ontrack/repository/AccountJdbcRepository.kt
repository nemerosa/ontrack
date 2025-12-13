package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountNameAlreadyDefinedException
import net.nemerosa.ontrack.model.exceptions.AccountNotFoundException
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.SecurityRole
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
    dataSource: DataSource
) : AbstractJdbcRepository(dataSource), AccountRepository {

    private fun toAccount(rs: ResultSet): Account {
        return Account.of(
            fullName = rs.getString("fullName"),
            email = rs.getString("email"),
            // Only USER roles can be loaded from the database
            role = SecurityRole.USER,
        ).withId(id(rs))
    }

    override fun findAll(): Collection<Account> {
        return jdbcTemplate!!.query(
            "SELECT * FROM ACCOUNTS ORDER BY EMAIL"
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }.filterNotNull()
    }

    override fun newAccount(account: Account): Account {
        return try {
            val id = dbCreate(
                "INSERT INTO ACCOUNTS (FULLNAME, EMAIL) " +
                        "VALUES (:fullName, :email)",
                params("fullName", account.fullName)
                    .addValue("email", account.email)
            )
            account.withId(of(id))
        } catch (_: DuplicateKeyException) {
            throw AccountNameAlreadyDefinedException(account.email)
        }
    }

    override fun saveAccount(account: Account) {
        try {
            namedParameterJdbcTemplate!!.update(
                """
                    UPDATE ACCOUNTS SET FULLNAME = :fullName, EMAIL = :email
                    WHERE ID = :id
                """,
                params("id", account.id())
                    .addValue("fullName", account.fullName)
                    .addValue("email", account.email)
            )
        } catch (_: DuplicateKeyException) {
            throw AccountNameAlreadyDefinedException(account.email)
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

    override fun getAccount(accountId: ID): Account {
        return namedParameterJdbcTemplate!!.queryForObject(
            "SELECT * FROM ACCOUNTS WHERE ID = :id",
            params("id", accountId.value)
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        } ?: throw AccountNotFoundException(accountId.value)
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
            "SELECT * FROM ACCOUNTS WHERE LOWER(EMAIL) LIKE :filter ORDER BY EMAIL",
            params("filter", String.format("%%%s%%", StringUtils.lowerCase(token)))
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }
    }

    override fun getAccountsForGroup(accountGroup: AccountGroup): List<Account> {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT A.* FROM ACCOUNTS A 
                INNER JOIN ACCOUNT_GROUP_LINK L ON L.ACCOUNT = A.ID 
                WHERE L.ACCOUNTGROUP = :accountGroupId 
                ORDER BY A.EMAIL
            """,
            params("accountGroupId", accountGroup.id())
        ) { rs: ResultSet, _ ->
            toAccount(rs)
        }
    }

    override fun findAccountByName(email: String): Account? {
        return getFirstItem(
            "SELECT * FROM ACCOUNTS WHERE EMAIL = :email",
            params("email", email)
        ) { rs, _ ->
            toAccount(rs)
        }
    }

    override fun findOrCreateAccount(account: Account): Account {
        return namedParameterJdbcTemplate!!.query(
            """
                INSERT INTO ACCOUNTS (FULLNAME, EMAIL)
                VALUES (:fullName, :email)
                ON CONFLICT (EMAIL)
                DO UPDATE SET EMAIL = EXCLUDED.EMAIL
                RETURNING *
            """.trimIndent(),
            params("fullName", account.fullName)
                .addValue("email", account.email)
        ) { rs, _ -> toAccount(rs) }
            .firstOrNull()
            ?: error("Cannot get or create account")
    }
}