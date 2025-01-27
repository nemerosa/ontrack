package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.api.BuildSearchExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class BuildSearchEnvironmentExtension(
    environmentsExtensionFeature: EnvironmentsExtensionFeature,
) : AbstractExtension(environmentsExtensionFeature), BuildSearchExtension {

    override val id: String = "environment"

    override fun contribute(
        value: String,
        tables: MutableList<String>,
        criteria: MutableList<String>,
        params: MutableMap<String, Any?>
    ) {
        tables +=
            """
                INNER JOIN ENV_SLOT_PIPELINE EVP ON EVP.BUILD_ID = B.ID
                INNER JOIN ENV_SLOTS EVS ON EVS.ID = EVP.SLOT_ID
                INNER JOIN ENVIRONMENTS EV ON EV.ID = EVS.ENVIRONMENT_ID
            """.trimIndent()

        criteria += "EV.NAME = :environmentName"
        criteria += "EVP.STATUS = 'DONE'"
        criteria += """
            EVP.ID = (
                SELECT ID
                FROM ENV_SLOT_PIPELINE
                WHERE SLOT_ID = EVS.ID
                AND STATUS = 'DONE'
                ORDER BY START DESC
                LIMIT 1
            )
        """.trimIndent()

        params["environmentName"] = value
    }
}