package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX,
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false)
@Component
class InfluxDBExtensionUserMenuExtension(extensionFeature: InfluxDBExtensionFeature) :
    AbstractExtension(extensionFeature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction> = ApplicationManagement::class.java

    override val action: Action = Action.of(
        id = "status",
        name = "InfluxDB status",
        uri = "status"
    ).withGroup(UserMenuExtensionGroups.system)
}