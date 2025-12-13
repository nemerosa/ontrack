package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import org.springframework.stereotype.Component

@Component
class TestTemplatingContextHandler : AbstractTemplatingContextHandler<TestTemplatingContextData>(
    TestTemplatingContextData::class
) {

    override val id: String = "test"

    override fun render(
        data: TestTemplatingContextData,
        field: String?,
        config: Map<String, String>,
        renderer: EventRenderer
    ): String = when (field) {
        "id" -> data.id
        "url" -> "mock://${data.id}"
        else -> throw TemplatingContextHandlerFieldNotManagedException(this, field)
    }
}