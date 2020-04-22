package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.sql.DataSource

@Repository
class TokensJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), TokensRepository {

    override fun invalidate(id: Int) {
        namedParameterJdbcTemplate!!.update(
                "DELETE FROM TOKENS WHERE ACCOUNT :id",
                params("id", id)
        )
    }

    override fun save(id: Int, encodedToken: String, time: LocalDateTime) {
        invalidate(id)
        namedParameterJdbcTemplate!!.update(
                "INSERT INTO TOKENS (ACCOUNT, VALUE, CREATION) VALUES (:id, :token, :creation)",
                params("id", id)
                        .addValue("token", encodedToken)
                        .addValue("creation", dateTimeForDB(time))
        )
    }
}
