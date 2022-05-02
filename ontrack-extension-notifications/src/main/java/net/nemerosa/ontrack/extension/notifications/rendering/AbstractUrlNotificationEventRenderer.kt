package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.model.events.AbstractEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties

abstract class AbstractUrlNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractEventRenderer() {

    protected val url: String = ontrackConfigProperties.url.trimEnd('/')

    protected fun getUrl(relativeURI: String): String =
        "$url/$relativeURI"

}