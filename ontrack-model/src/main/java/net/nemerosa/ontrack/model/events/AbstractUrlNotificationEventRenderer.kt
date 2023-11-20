package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.support.OntrackConfigProperties

abstract class AbstractUrlNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractEventRenderer() {

    protected val url: String = ontrackConfigProperties.url.trimEnd('/')

    protected fun getUrl(relativeURI: String): String =
        "$url/$relativeURI"

}