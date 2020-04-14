package db.migration

import net.nemerosa.ontrack.model.structure.Description
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Suppress("ClassName")
@Component
class V29__756_BuiltinGroups : BaseJavaMigration() {

    private val logger = LoggerFactory.getLogger(V29__756_BuiltinGroups::class.java)

    override fun migrate(context: Context) {
        val jdbcTemplate = JdbcTemplate(context.configuration.dataSource)
        logger.info("Checking the creation of built-in account groups...")
        val groups = jdbcTemplate.queryForList("SELECT * FROM ACCOUNT_GROUPS")
        if (groups.isNotEmpty()) {
            logger.info("Not creating built-in account groups since some groups are already defined.")
        } else {
            logger.info("Creating built-in account groups...")
            createGroup(jdbcTemplate, "Administrators", "Group of administrators", false)
            createGroup(jdbcTemplate, "Read-Only", "Read-only users", true)
        }
    }

    private fun createGroup(jdbcTemplate: JdbcTemplate, name: String, description: String, autoJoin: Boolean) {
        logger.info("Creating $name built-in group...")
        jdbcTemplate.update("INSERT INTO ACCOUNT_GROUPS(NAME, DESCRIPTION, AUTOJOIN) VALUES (?, ?, ?)") { ps ->
            ps.setString(1, name)
            ps.setString(2, description)
            ps.setBoolean(3, autoJoin)
        }
    }

}