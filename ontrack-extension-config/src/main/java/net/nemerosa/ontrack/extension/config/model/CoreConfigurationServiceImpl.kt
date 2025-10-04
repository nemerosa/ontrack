package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.license.ConfigurationLicense
import net.nemerosa.ontrack.extension.config.scm.SCMEngine
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CoreConfigurationServiceImpl(
    private val configurationLicense: ConfigurationLicense,
    private val securityService: SecurityService,
    private val structureService: StructureService,
    private val propertyService: PropertyService,
) : CoreConfigurationService {

    override fun configureProject(
        configuration: ConfigurationInput,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Project {
        configurationLicense.checkConfigurationFeatureEnabled()
        val projectName = ciEngine.getProjectName(env)
            ?: scmEngine.getProjectName(env)
            ?: throw CoreConfigurationException("Could not get the project name from the environment")

        val existingProject = securityService.asAdmin { structureService.findProjectByName(projectName) }.getOrNull()
        val project = if (existingProject != null) {
            if (securityService.isProjectFunctionGranted<ProjectConfig>(existingProject)) {
                existingProject
            } else {
                throw CoreConfigurationException("Project $projectName already exists but you do not have the `PROJECT_CONFIG` permission.")
            }
        } else {
            structureService.newProject(Project.of(NameDescription(name = projectName, description = null)))
        }

        // Configuration of properties
        configureProperties(
            entity = project,
            defaults = configuration.configuration.defaults.project.properties,
            // TODO Custom properties
        )

        // TODO Configuration of the project SCM

        return project
    }

    private fun configureProperties(
        entity: ProjectEntity,
        defaults: List<PropertyConfiguration>,
    ) {
        defaults.forEach { config ->
            propertyService.editProperty(
                entity,
                config.type,
                config.data
            )
        }
    }

    fun configureBuild() {
        configurationLicense.checkConfigurationFeatureEnabled()
    }

}