package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDoc
import net.nemerosa.ontrack.model.templating.TemplatingRenderableDocField
import org.springframework.stereotype.Component
import kotlin.reflect.full.findAnnotation

@Component
@APIDescription("The `deployment` context is injected into workflows triggered by deployments being started, run or completed.")
class DeploymentTemplatingContextHandlerDoc(
    val fieldHandlers: List<DeploymentTemplatingContextFieldHandler>,
) : TemplatingRenderableDoc {

    override val id: String = "deployment"
    override val displayName: String = "Deployment context"
    override val contextName: String = "Environments"
    override val fields: List<TemplatingRenderableDocField>
        get() = fieldHandlers.map {
            getTemplatingRenderableDocField(it)
        }

    private fun getTemplatingRenderableDocField(fieldHandler: DeploymentTemplatingContextFieldHandler): TemplatingRenderableDocField {
        return TemplatingRenderableDocField(
            name = fieldHandler.field,
            description = getTypeDescription(fieldHandler::class),
            config = fieldHandler::class.findAnnotation<Documentation>()
                ?.value
        )
    }
}