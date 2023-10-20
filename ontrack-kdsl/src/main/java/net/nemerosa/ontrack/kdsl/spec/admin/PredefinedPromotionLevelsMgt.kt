package net.nemerosa.ontrack.kdsl.spec.admin

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.PredefinedPromotionLevelByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import java.net.URL

/**
 * Management of the predefined promotion levels
 */
class PredefinedPromotionLevelsMgt(connector: Connector) : Connected(connector) {

    /**
     * Creates a predefined promotion level if it does not exist yet.
     *
     * @param name Name of the promotion level
     * @param description Description of the promotion level
     * @param image URL to the image to set
     */
    fun createPredefinedPromotionLevel(
        name: String,
        description: String = "",
        image: URL? = null
    ) {
        // Gets an existing predefined promotion level by name
        val existing = graphqlConnector.query(
            PredefinedPromotionLevelByNameQuery(name)
        )?.predefinedPromotionLevelByName()
        // Creating only if not existing
        if (existing == null) {
            val pplId = connector.post(
                "/rest/admin/predefinedPromotionLevels/create",
                body = mapOf(
                    "name" to name,
                    "description" to description,
                )
            ).body.asJson().path("id").asInt()
            // Image
            if (image != null) {
                val imageBytes = image.readBytes()
                connector.uploadFile(
                    "/rest/admin/predefinedPromotionLevels/${pplId}/image",
                    headers = emptyMap(),
                    "file" to imageBytes,
                )
            }
        }
    }

}