package net.nemerosa.ontrack.kdsl.spec.admin

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.parse
import java.net.URLEncoder

/**
 * Admin interface to Ontrack.
 */
class AdminMgt(connector: Connector) : Connected(connector) {

    /**
     * Gets a list of log entries
     *
     * @param text Filter on text message
     * @param count Number of entries to return
     */
    fun logEntries(text: String = "", count: Int = 1): List<LogEntry> =
        connector.get("/rest/admin/logs?count=$count&text=${URLEncoder.encode(text, "UTF-8")}")
            .body
            .parse<LogEntries>()
            .resources

    /**
     * Mgt. of predefined promotion levels
     */
    val predefinedPromotionLevels: PredefinedPromotionLevelsMgt by lazy {
        PredefinedPromotionLevelsMgt(connector)
    }

}