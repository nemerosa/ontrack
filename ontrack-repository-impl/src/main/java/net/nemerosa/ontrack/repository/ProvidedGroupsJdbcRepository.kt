package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import javax.sql.DataSource

/**
 * Maximum length for a group name
 */
const val GROUP_NAME_MAX_LENGTH = 80

@Repository
class ProvidedGroupsJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), ProvidedGroupsRepository {

    override fun saveProvidedGroups(account: Int, source: AuthenticationSource, groups: Set<String>) {
        // Removes all previous groups
        namedParameterJdbcTemplate!!.update(
                "DELETE FROM PROVIDED_GROUPS WHERE ACCOUNT = :account",
                params("account", account)
        )
        // Adds the groups
        val params = source.asParams().addValue("account", account)
        namedParameterJdbcTemplate!!.batchUpdate(
                "INSERT INTO PROVIDED_GROUPS(ACCOUNT, PROVIDER, SOURCE, GROUP_NAME) VALUES (:account, :provider, :source, :groupName)",
                groups.filter { group ->
                    group.length <= GROUP_NAME_MAX_LENGTH
                }.map { group ->
                    MapSqlParameterSource(params.values).addValue("groupName", group)
                }.toTypedArray()
        )
    }

    override fun getProvidedGroups(account: Int, source: AuthenticationSource): Set<String> =
            namedParameterJdbcTemplate!!.queryForList(
                    "SELECT GROUP_NAME FROM PROVIDED_GROUPS WHERE ACCOUNT = :account AND PROVIDER = :provider AND SOURCE = :source",
                    source.asParams().addValue("account", account),
                    String::class.java
            ).toSet()

    override fun getSuggestedGroups(source: AuthenticationSource, token: String): List<String> =
            namedParameterJdbcTemplate!!.queryForList(
                    "SELECT GROUP_NAME FROM PROVIDED_GROUPS WHERE PROVIDER = :provider AND SOURCE = :source AND LOWER(GROUP_NAME) LIKE :token ORDER BY GROUP_NAME",
                    source.asParams().addValue("token", "%${token.toLowerCase()}%"),
                    String::class.java
            )

}