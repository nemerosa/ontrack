package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.schema.authorizations.Authorization
import net.nemerosa.ontrack.graphql.schema.authorizations.Authorizations
import net.nemerosa.ontrack.graphql.schema.authorizations.isProjectFunctionGranted
import net.nemerosa.ontrack.model.security.BranchCreate
import net.nemerosa.ontrack.model.security.ProjectDelete
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class ProjectAuthorizations(
        private val securityService: SecurityService
) : Authorizations<Project> {

    override val targetType: KClass<Project> = Project::class

    override val authorizations: List<Authorization<Project>> = listOf(
            // Project itself
            Authorization("updateProject", "Updating this project") {
                securityService.isProjectFunctionGranted<ProjectEdit>(it)
            },
            Authorization("deleteProject", "Deleting this project") {
                securityService.isProjectFunctionGranted<ProjectDelete>(it)
            },
            Authorization("disableProject", "Disabling this project") {
                !it.isDisabled && securityService.isProjectFunctionGranted<ProjectDelete>(it)
            },
            Authorization("enableProject", "Disabling this project") {
                it.isDisabled && securityService.isProjectFunctionGranted<ProjectDelete>(it)
            },
            // Branches
            Authorization("createBranch", "Creating a branch in this project") {
                !it.isDisabled && securityService.isProjectFunctionGranted<BranchCreate>(it)
            }
    )
}