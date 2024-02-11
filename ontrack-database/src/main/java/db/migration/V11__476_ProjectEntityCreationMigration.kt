package db.migration

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventType
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCallback
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


@Suppress("ClassName")
@Component
class V11__476_ProjectEntityCreationMigration : BaseJavaMigration() {

    private val logger: Logger = LoggerFactory.getLogger(V11__476_ProjectEntityCreationMigration::class.java)

    override fun migrate(context: Context) {
        val jdbcTemplate = JdbcTemplate(context.configuration.dataSource)
        logger.info("Migrating the project entity creation events...")
        migrateCreation(jdbcTemplate, EventFactory.NEW_PROJECT, "PROJECT", "PROJECTS")
        migrateCreation(jdbcTemplate, EventFactory.NEW_BRANCH, "BRANCH", "BRANCHES")
        migrateCreation(jdbcTemplate, EventFactory.NEW_PROMOTION_LEVEL, "PROMOTION_LEVEL", "PROMOTION_LEVELS")
        migrateCreation(jdbcTemplate, EventFactory.NEW_VALIDATION_STAMP, "VALIDATION_STAMP", "VALIDATION_STAMPS")
    }

    private fun migrateCreation(
        jdbcTemplate: JdbcTemplate,
        eventType: EventType,
        eventLinkColumn: String,
        entityTable: String
    ) {
        jdbcTemplate.execute(
            { connection: Connection ->
                connection.prepareStatement(
                    String.format("SELECT * FROM %s", entityTable),
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE
                )
            },
            PreparedStatementCallback<Void?> { psEntity: PreparedStatement ->
                psEntity.executeQuery().use { rsEntity ->
                    while (rsEntity.next()) {
                        // Gets the entity ID
                        val entityId = rsEntity.getInt("ID")
                        // Statement for the events
                        jdbcTemplate.execute(
                            { connection: Connection ->
                                connection.prepareStatement(
                                    String.format(
                                        "SELECT EVENT_TIME, EVENT_USER FROM EVENTS WHERE %s = ? AND EVENT_TYPE = ?",
                                        eventLinkColumn
                                    )
                                )
                            },
                            PreparedStatementCallback<Void?> { psEvent: PreparedStatement ->
                                psEvent.setInt(1, entityId)
                                psEvent.setString(2, eventType.id)
                                psEvent.executeQuery().use { rsEvent ->
                                    if (rsEvent.next()) {
                                        val creation = rsEvent.getString(1)
                                        val creator = rsEvent.getString(2)
                                        // Update of the project entity
                                        rsEntity.updateString("CREATION", creation)
                                        rsEntity.updateString("CREATOR", creator)
                                        rsEntity.updateRow()
                                    }
                                }
                                null
                            }
                        )
                    }
                    return@PreparedStatementCallback null
                }
            }
        )
    }
}