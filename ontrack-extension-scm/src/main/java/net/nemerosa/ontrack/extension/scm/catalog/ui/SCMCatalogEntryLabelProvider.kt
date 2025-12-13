package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class SCMCatalogEntryLabelProvider(
    private val catalogLinkService: CatalogLinkService,
    private val scmExtensionConfigProperties: SCMExtensionConfigProperties,
) : LabelProvider {

    override val name: String = "SCM Catalog Entry"

    override val isEnabled: Boolean = true

    override fun getLabelsForProject(project: Project): List<LabelForm> =
        if (scmExtensionConfigProperties.catalog.enabled) {
            listOf(
                catalogLinkService.getSCMCatalogEntry(project)?.run {
                    LabelForm(
                        category = LABEL_CATEGORY,
                        name = LABEL_NAME_ENTRY,
                        description = "This project is associated with a SCM catalog entry",
                        color = "#33cc33"
                    )
                } ?: LabelForm(
                    category = LABEL_CATEGORY,
                    name = LABEL_NAME_NO_ENTRY,
                    description = "This project is NOT associated with a SCM catalog entry",
                    color = "#a9a9a9"
                )
            )
        } else {
            emptyList()
        }

    companion object {
        const val LABEL_CATEGORY = "scm-catalog"
        const val LABEL_NAME_ENTRY = "entry"
        const val LABEL_NAME_NO_ENTRY = "no-entry"
    }
}