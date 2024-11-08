package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityPageBuilder
import net.nemerosa.ontrack.model.support.OntrackConfigProperties

abstract class AbstractUrlNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractEventRenderer() {

    protected val url: String = ontrackConfigProperties.url.trimEnd('/')

    protected fun getUrl(relativeURI: String): String =
        // TODO Use UILocations
        "$url/$relativeURI"

    final override fun render(projectEntity: ProjectEntity, name: String): String =
        renderLink(
            text = name,
            href = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity)),
        )

}