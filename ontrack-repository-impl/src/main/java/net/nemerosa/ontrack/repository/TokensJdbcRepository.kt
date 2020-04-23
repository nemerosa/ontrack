package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.structure.Token
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
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

    override fun save(id: Int, token: String, time: LocalDateTime) {
        invalidate(id)
        namedParameterJdbcTemplate!!.update(
                "INSERT INTO TOKENS (ACCOUNT, VALUE, CREATION) VALUES (:id, :token, :creation)",
                params("id", id)
                        .addValue("token", token)
                        .addValue("creation", dateTimeForDB(time))
        )
    }

    override fun getForAccount(account: Account): Token? {
        return getFirstItem(
                "SELECT * FROM TOKENS WHERE ACCOUNT = :id",
                params("id", account.id())
        ) { rs, _ ->
            Token(
                    value = rs.getString("VALUE"),
                    creation = dateTimeFromDB(rs.getString("CREATION")),
                    validUntil = null
            )
        }
    }
}
