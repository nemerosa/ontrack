package net.nemerosa.ontrack.model.structure

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