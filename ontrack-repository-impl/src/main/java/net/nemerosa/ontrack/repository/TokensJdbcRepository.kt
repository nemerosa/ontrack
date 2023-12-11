package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.model.structure.TokenScope
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import javax.sql.DataSource

@Repository
class TokensJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), TokensRepository {

    override fun invalidate(id: Int, name: String): String? {
        val params = mapOf(
            "id" to id,
            "name" to name,
        )
        val previous: String? = getFirstItem(
            "SELECT VALUE FROM TOKENS WHERE ACCOUNT = :id AND NAME = :name",
            params,
            String::class.java
        )
        namedParameterJdbcTemplate!!.update(
            "DELETE FROM TOKENS WHERE ACCOUNT = :id AND NAME = :name",
            params
        )
        return previous
    }

    override fun save(
        id: Int,
        name: String,
        token: String,
        scope: TokenScope,
        time: LocalDateTime,
        until: LocalDateTime?
    ) {
        invalidate(id, name)
        namedParameterJdbcTemplate!!.update(
            "INSERT INTO TOKENS (ACCOUNT, NAME, VALUE, SCOPE, CREATION, VALID_UNTIL) VALUES (:id, :name, :token, :scope, :creation, :until)",
            params("id", id)
                .addValue("name", name)
                .addValue("token", token)
                .addValue("scope", scope.name)
                .addValue("creation", dateTimeForDB(time))
                .addValue("until", dateTimeForDB(until))
        )
    }

    override fun getTokenForAccount(account: Account, name: String): Token? {
        return getFirstItem(
            "SELECT * FROM TOKENS WHERE ACCOUNT = :id AND NAME = :name",
            params("id", account.id()).addValue("name", name)
        ) { rs, _ ->
            toToken(rs)
        }
    }

    override fun findAccountByToken(token: String): Pair<Int, Token>? = getFirstItem(
        "SELECT * FROM TOKENS WHERE VALUE = :token",
        params("token", token)
    ) { rs, _ ->
        rs.getInt("ACCOUNT") to toToken(rs)
    }

    override fun revokeAll(): Int {
        @Suppress("SqlWithoutWhere")
        return jdbcTemplate!!.update("DELETE FROM TOKENS")
    }

    override fun getTokens(account: Account): List<Token> {
        return namedParameterJdbcTemplate!!.query(
            "SELECT * FROM TOKENS WHERE ACCOUNT = :account ORDER BY NAME",
            mapOf("account" to account.id())
        ) { rs, _ ->
            toToken(rs)
        }
    }

    override fun invalidateAll(accountId: Int): List<String> {
        val params = mapOf(
            "account" to accountId,
        )
        val previous = mutableListOf<String>()
        namedParameterJdbcTemplate!!.query(
            "SELECT VALUE FROM TOKENS WHERE ACCOUNT = :account",
            params
        ) { rs ->
            previous += rs.getString("VALUE")
        }
        namedParameterJdbcTemplate!!.update(
            "DELETE FROM TOKENS WHERE ACCOUNT = :account",
            params
        )
        return previous
    }

    override fun updateLastUsed(token: Token, lastUsed: LocalDateTime) {
        namedParameterJdbcTemplate!!.update(
            "UPDATE TOKENS SET LAST_USED = :lastUsed WHERE VALUE = :token",
            mapOf(
                "lastUsed" to dateTimeForDB(lastUsed),
                "token" to token.value,
            )
        )
    }

    private fun toToken(rs: ResultSet): Token {
        return Token(
            name = rs.getString("NAME"),
            value = rs.getString("VALUE"),
            scope = TokenScope.valueOf(rs.getString("SCOPE")),
            creation = dateTimeFromDB(rs.getString("CREATION"))!!,
            validUntil = dateTimeFromDB(rs.getString("VALID_UNTIL")),
            lastUsed = dateTimeFromDB(rs.getString("LAST_USED")),
        )
    }
}
