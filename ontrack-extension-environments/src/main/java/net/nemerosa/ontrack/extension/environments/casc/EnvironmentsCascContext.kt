package net.nemerosa.ontrack.extension.environments.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class EnvironmentsCascContext(
    private val environmentService: EnvironmentService,
) : AbstractCascContext(), SubConfigContext {

    override val field: String = "environments"

    override val type: CascType = cascObject(EnvironmentsCascModel::class)

    override fun run(node: JsonNode, paths: List<String>) {
        val model: EnvironmentsCascModel = node.parse()
        run(model)
    }

    private fun run(model: EnvironmentsCascModel) {
        runEnvironments(model)
    }

    private fun runEnvironments(model: EnvironmentsCascModel) {
        val existingEnvs = environmentService.findAll()
        syncForward(
            from = model.environments,
            to = existingEnvs,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { env ->
                environmentService.save(
                    Environment(
                        name = env.name,
                        description = env.description,
                        order = env.order,
                        tags = env.tags,
                    )
                )
            }
            onModification { env, existing ->
                val adapted = Environment(
                    id = existing.id,
                    name = existing.name,
                    description = env.description,
                    order = env.order,
                    tags = env.tags,
                )
                environmentService.save(adapted)
            }
            onDeletion { existing ->
                if (!model.keepEnvironments) {
                    environmentService.delete(existing)
                }
            }
        }
    }

    override fun render(): JsonNode =
        EnvironmentsCascModel(
            keepEnvironments = true, // Always true when rendering
            environments = environmentService.findAll().map {
                EnvironmentCasc(
                    name = it.name,
                    description = it.description ?: "",
                    order = it.order,
                    tags = it.tags,
                )
            }
        ).asJson()

}