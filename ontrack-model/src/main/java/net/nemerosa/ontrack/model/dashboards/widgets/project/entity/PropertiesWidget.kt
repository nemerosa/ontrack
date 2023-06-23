package net.nemerosa.ontrack.model.dashboards.widgets.project.entity

import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.model.dashboards.WidgetInstance
import net.nemerosa.ontrack.model.dashboards.widgets.AbstractWidget
import net.nemerosa.ontrack.model.dashboards.widgets.WidgetConfig
import org.springframework.stereotype.Component

@Component
class PropertiesWidget : AbstractWidget<PropertiesWidget.PropertiesWidgetConfig>(
    key = "project/entity/Properties",
    name = "List of properties"
) {
    fun toInstance() = WidgetInstance(
        key = key,
        config = NullNode.instance,
    )

    class PropertiesWidgetConfig : WidgetConfig

}