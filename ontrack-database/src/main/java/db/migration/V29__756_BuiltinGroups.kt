package db.migration

import net.nemerosa.ontrack.model.security.Roles
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component

@Suppress("ClassName")
@Component
class V29__756_BuiltinGroups : BaseJavaMigration() {

    companion object {
        const val ADMIN_GROUP = "Administrators"
        const val READ_ONLY_GROUP = "Read-Only"
        const val PARTICIPANT_GROUP = "Participants"
    }

    private val logger = LoggerFactory.getLogger(V29__756_BuiltinGroups::class.java)

    override fun migrate(context: Context) {
        val jdbcTemplate = JdbcTemplate(context.configuration.dataSource)
        logger.info("Checking the creation of built-in account groups...")
        val groups = jdbcTemplate.queryForList("SELECT * FROM ACCOUNT_GROUPS")
        if (groups.isNotEmpty()) {
            logger.info("Not creating built-in account groups since some groups are already defined.")
        } else {
            logger.info("Creating built-in account groups...")
            val adminId = createGroup(jdbcTemplate, ADMIN_GROUP, "Group of administrators", false)
            val readOnlyId = createGroup(jdbcTemplate, READ_ONLY_GROUP, "Read-only users", false)
            val participantId = createGroup(jdbcTemplate, PARTICIPANT_GROUP, "Users which can add comments on validation runs.", false)
            logger.info("Creating built-in global permissions...")
            createGlobalPermissions(jdbcTemplate, adminId, Roles.GLOBAL_ADMINISTRATOR)
            createGlobalPermissions(jdbcTemplate, readOnlyId, Roles.GLOBAL_READ_ONLY)
        }
    }

    private fun createGlobalPermissions(jdbcTemplate: JdbcTemplate, group: Int, role: String) {
        jdbcTemplate.update("INSERT INTO GROUP_GLOBAL_AUTHORIZATIONS (ACCOUNTGROUP, ROLE) VALUES (?, ?)") { ps ->
            ps.setInt(1, group)
            ps.setString(2, role)
        }
    }

    private fun createGroup(jdbcTemplate: JdbcTemplate, name: String, description: String, autoJoin: Boolean): Int {
        logger.info("Creating $name built-in group...")
        val keyHolder = GeneratedKeyHolder()
        val psc = PreparedStatementCreator { con ->
            con.prepareStatement("INSERT INTO ACCOUNT_GROUPS(NAME, DESCRIPTION, AUTOJOIN) VALUES (?, ?, ?)", arrayOf("id")).apply {
                setString(1, name)
                setString(2, description)
                setBoolean(3, autoJoin)
            }
        }
        jdbcTemplate.update(psc, keyHolder)
        return keyHolder.key?.toInt() ?: throw IllegalStateException("Cannot get generated key")
    }

}