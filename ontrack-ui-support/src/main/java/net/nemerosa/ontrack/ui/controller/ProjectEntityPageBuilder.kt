package net.nemerosa.ontrack.ui.controller

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.apache.commons.text.CaseUtils
import java.util.*

object ProjectEntityPageBuilder {

    fun getEntityPageName(projectEntityType: ProjectEntityType): String =
        CaseUtils.toCamelCase(
            projectEntityType.name.lowercase(Locale.getDefault()),
            false,
            '_'
        )

    fun getEntityPage(entity: ProjectEntity): String =
        "${getEntityPageName(entity.projectEntityType)}/${entity.id()}"

    fun getEntityPageRelativeURI(entity: ProjectEntity): String =
        "#/${getEntityPage(entity)}"
}