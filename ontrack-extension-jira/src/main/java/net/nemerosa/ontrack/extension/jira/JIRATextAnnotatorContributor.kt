package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotator
import org.springframework.stereotype.Component

@Component
class JIRATextAnnotatorContributor : FreeTextAnnotatorContributor {
    override fun getMessageAnnotator(entity: ProjectEntity): MessageAnnotator? {
        // Gets the project for this entity
        val project: Project = entity.project
        // TODO Gets any issue service extension for this project
        // TODO Checks this is an actual JIRA service extension
        // TODO Returns a message annotator based on this JIRA service extension
        TODO("Needs to get a JIRA issue service from a project")
    }
}