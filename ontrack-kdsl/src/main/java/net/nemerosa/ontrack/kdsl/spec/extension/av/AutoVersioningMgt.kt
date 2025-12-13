package net.nemerosa.ontrack.kdsl.spec.extension.av

import com.apollographql.apollo.api.Optional
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.AutoVersioningScheduleMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.connector.parse
import java.time.LocalDateTime

class AutoVersioningMgt(connector: Connector) : Connected(connector) {

    val stats: AutoVersioningStats
        get() = connector.get("/extension/auto-versioning/stats").body.parse()

    val audit: AutoVersioningAuditMgt = AutoVersioningAuditMgt(connector)

    /**
     * Forcing the scheduling of the auto-versioning at a given time.
     */
    fun schedule(time: LocalDateTime?) {
        graphqlConnector.mutate(
            AutoVersioningScheduleMutation(
                Optional.presentIfNotNull(time)
            )
        ) { it?.scheduleAutoVersioning?.payloadUserErrors?.convert() }
    }

}