package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotator
import org.springframework.stereotype.Component

@Component
class GitHubFreeTextAnnotatorContributor: FreeTextAnnotatorContributor {
    override fun getMessageAnnotators(entity: ProjectEntity): List<MessageAnnotator> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}