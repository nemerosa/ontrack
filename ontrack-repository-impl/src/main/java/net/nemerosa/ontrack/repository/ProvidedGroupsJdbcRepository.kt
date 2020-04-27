package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ProvidedGroupsJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), ProvidedGroupsRepository {

    override fun saveProvidedGroups(account: Int, source: AuthenticationSource, groups: Set<String>) {
        // Removes all previous groups
        namedParameterJdbcTemplate!!.update(
                "DELETE FROM PROVIDED_GROUPS WHERE ACCOUNT = :account",
                params("account", account)
        )
        // Adds the groups
        val params = params("account", account).addValue("mode", source.id)
        namedParameterJdbcTemplate!!.batchUpdate(
                "INSERT INTO PROVIDED_GROUPS(ACCOUNT, MODE, GROUP_NAME) VALUES (:account, :mode, :groupName)",
                groups.map { group ->
                    MapSqlParameterSource(params.values).addValue("group", group)
                }.toTypedArray()
        )
    }

    override fun getProvidedGroups(account: Int, source: AuthenticationSource): Set<String> =
            namedParameterJdbcTemplate!!.queryForList(
                    "SELECT GROUP_NAME FROM PROVIDED_GROUPS WHERE ACCOUNT = :account AND MODE = :mode",
                    params("account", account).addValue("mode", source.id),
                    String::class.java
            ).toSet()

}