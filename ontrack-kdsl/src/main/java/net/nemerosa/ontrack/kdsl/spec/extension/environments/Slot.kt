package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.CreatePipelineMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.Resource

class Slot(
    connector: Connector,
    val id: String,
    val environment: Environment,
    val project: Project,
    val qualifier: String = "",
    val description: String = "",
) : Resource(connector) {

    fun createPipeline(build: Build): SlotPipeline {
        val pipeline = graphqlConnector.mutate(
            CreatePipelineMutation(
                id,
                build.id.toInt(),
            )
        ) { it?.startSlotPipeline()?.fragments()?.payloadUserErrors()?.convert() }
            ?.startSlotPipeline()?.pipeline()
            ?: error("Cannot get the create pipeline")
        return SlotPipeline(
            connector = connector,
            id = pipeline.id(),
            number = pipeline.number() ?: 1,
            slot = this,
            build = build,
            status = pipeline.status(),
        )
    }

}
