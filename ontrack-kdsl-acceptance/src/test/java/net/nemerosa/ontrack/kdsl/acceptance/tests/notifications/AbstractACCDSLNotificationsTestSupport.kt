package net.nemerosa.ontrack.kdsl.acceptance.tests.notifications

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.ProjectEntity
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications

abstract class AbstractACCDSLNotificationsTestSupport : AbstractACCDSLTestSupport() {

    /**
     * Subscription for a project entity.
     */
    protected fun ProjectEntity.subscribe(
        channel: String,
        channelConfig: Any,
        keywords: String?,
        events: List<String>,
        contentTemplate: String? = null,
    ) {
        ontrack.notifications.subscribe(
            channel,
            channelConfig,
            keywords,
            events,
            projectEntity = this,
            contentTemplate = contentTemplate,
        )
    }

}