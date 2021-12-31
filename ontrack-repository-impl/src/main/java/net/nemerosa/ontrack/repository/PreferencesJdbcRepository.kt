package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class PreferencesJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), PreferencesRepository {

    override fun getPreferences(accountId: Int): JsonNode? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT CONTENT
                FROM PREFERENCES
                WHERE ACCOUNTID = :accountId
            """,
            mapOf("accountId" to accountId)
        ) { rs, _ ->
            readJson(rs, "CONTENT")
        }.firstOrNull()

}