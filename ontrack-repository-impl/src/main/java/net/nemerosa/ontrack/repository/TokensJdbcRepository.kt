package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import javax.sql.DataSource

@Repository
class TokensJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), TokensRepository {

    override fun invalidate(id: Int) {
        namedParameterJdbcTemplate!!.update(
                "DELETE FROM TOKENS WHERE ACCOUNT = :id",
                params("id", id)
        )
    }

    override fun save(id: Int, token: String, time: LocalDateTime, until: LocalDateTime?) {
        invalidate(id)
        namedParameterJdbcTemplate!!.update(
                "INSERT INTO TOKENS (ACCOUNT, VALUE, CREATION, VALID_UNTIL) VALUES (:id, :token, :creation, :until)",
                params("id", id)
                        .addValue("token", token)
                        .addValue("creation", dateTimeForDB(time))
                        .addValue("until", dateTimeForDB(until))
        )
    }

    override fun getForAccount(account: Account): Token? {
        return getFirstItem(
                "SELECT * FROM TOKENS WHERE ACCOUNT = :id",
                params("id", account.id())
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

    private fun toToken(rs: ResultSet): Token {
        return Token(
                value = rs.getString("VALUE"),
                creation = dateTimeFromDB(rs.getString("CREATION"))!!,
                validUntil = dateTimeFromDB(rs.getString("VALID_UNTIL"))
        )
    }
}
