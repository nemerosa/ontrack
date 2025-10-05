package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.license.ConfigurationLicense
import net.nemerosa.ontrack.extension.config.scm.SCMEngine
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.jvm.optionals.getOrNull

@Service
class CoreConfigurationServiceImpl(
    private val configurationLicense: ConfigurationLicense,
    private val securityService: SecurityService,
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val buildDisplayNameService: BuildDisplayNameService,
    private val validationDataTypeService: ValidationDataTypeService,
    private val promotionLevelConfigurators: List<PromotionLevelConfigurator>,
) : CoreConfigurationService {

    override fun configureProject(
        input: ConfigurationInput,
        configuration: ProjectConfiguration,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Project {
        configurationLicense.checkConfigurationFeatureEnabled()
        val projectName = ciEngine.getProjectName(env)
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

        // Configuration of the project SCM (using the SCM engine)
        scmEngine.configureProject(project, configuration, env)

        // Configuration of properties
        configureProperties(
            entity = project,
            defaults = configuration.properties,
        )

        return project
    }

    override fun configureBranch(
        project: Project,
        input: ConfigurationInput,
        configuration: BranchConfiguration,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Branch {
        configurationLicense.checkConfigurationFeatureEnabled()
        val rawBranchName = ciEngine.getBranchName(env)
            ?: throw CoreConfigurationException("Could not get the branch name from the environment")

        val branchName = scmEngine.normalizeBranchName(rawBranchName)

        val branch = structureService.findBranchByName(project.name, branchName).getOrNull()
            ?: structureService.newBranch(
                Branch.of(
                    project,
                    NameDescription(name = branchName, description = null)
                )
            )

        configureValidations(branch, configuration.validations)
        configurePromotions(branch, configuration.promotions)

        // Configuration of the branch SCM (using the SCM engine)
        scmEngine.configureBranch(branch, configuration, env, rawBranchName)

        configureProperties(
            entity = branch,
            defaults = configuration.properties,
        )

        return branch
    }

    override fun configureBuild(
        branch: Branch,
        input: ConfigurationInput,
        configuration: BuildConfiguration,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Build {
        configurationLicense.checkConfigurationFeatureEnabled()

        val buildName = getBuildName(configuration, ciEngine, env)

        val build = structureService.findBuildByName(branch.project.name, branch.name, buildName).getOrNull()
            ?: structureService.newBuild(
                Build.of(
                    branch,
                    NameDescription(name = buildName, description = null),
                    securityService.currentSignature,
                )
            )

        // Configuration of the build SCM (using the SCM engine)
        scmEngine.configureBuild(build, configuration, env)

        configureProperties(
            entity = branch,
            defaults = configuration.properties,
        )

        // Build display name
        configureBuildDisplayName(build, configuration, ciEngine, env)

        return build
    }

    private fun configureValidations(
        branch: Branch,
        validations: List<ValidationStampConfiguration>,
    ) {
        validations.forEach { config ->
            val vs = structureService.setupValidationStamp(
                branch = branch,
                validationStampName = config.name,
                validationStampDescription = config.description,
            )
            if (config.validationStampDataConfiguration != null) {
                structureService.saveValidationStamp(
                    vs.withDataType(
                        validationDataTypeService.validateValidationDataTypeConfig<Any>(
                            config.validationStampDataConfiguration.type,
                            config.validationStampDataConfiguration.data
                        )
                    )
                )
            } else {
                structureService.saveValidationStamp(
                    vs.withDataType(null)
                )
            }
        }
    }

    private fun configurePromotions(
        branch: Branch,
        promotions: List<PromotionLevelConfiguration>,
    ) {
        promotions.forEach { config ->
            val pl = structureService.findPromotionLevelByName(
                project = branch.project.name,
                branch = branch.name,
                promotionLevel = config.name
            ).getOrNull() ?: structureService.newPromotionLevel(
                PromotionLevel.of(
                    branch,
                    NameDescription(name = config.name, description = config.description)
                )
            )
            // Auto promotion property is not accessible through the API (general extension not visible)
            promotionLevelConfigurators.forEach { configurator ->
                configurator.configure(pl, config)
            }
        }
    }

    private fun configureBuildDisplayName(
        build: Build,
        configuration: BuildConfiguration,
        ciEngine: CIEngine,
        env: Map<String, String>
    ) {
        val version = ciEngine.getBuildVersion(env)
        if (!version.isNullOrBlank()) {
            buildDisplayNameService.setDisplayName(build = build, displayName = version, override = false)
        }
    }

    private fun getBuildName(
        configuration: BuildConfiguration,
        ciEngine: CIEngine,
        env: Map<String, String>
    ): String {
        // TODO Configuration of the build name (template for example)
        val timestampUtc = Instant.now()
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

        // Suffix
        val suffix = ciEngine.getBuildSuffix(env) // TODO Consolidated configuration for the build

        return if (suffix.isNullOrBlank()) {
            timestampUtc
        } else {
            "$timestampUtc-$suffix"
        }
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

}