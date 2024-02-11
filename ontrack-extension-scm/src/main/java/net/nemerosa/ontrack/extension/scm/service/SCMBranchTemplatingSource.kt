package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import org.springframework.stereotype.Component

@Component
@APIDescription("Gets the SCM branch associated with a branch. Renders an empty string if there is none.")
@DocumentationExampleCode("${'$'}{branch.scmBranch}")
class SCMBranchTemplatingSource(
    private val scmDetector: SCMDetector,
) : AbstractTemplatingSource(
    field = "scmBranch",
    type = ProjectEntityType.BRANCH,
) {

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String =
        if (entity is Branch) {
            val scm = scmDetector.getSCM(entity.project)
            if (scm != null) {
                scm.getSCMBranch(entity) ?: ""
            } else {
                ""
            }
        } else {
            ""
        }

}