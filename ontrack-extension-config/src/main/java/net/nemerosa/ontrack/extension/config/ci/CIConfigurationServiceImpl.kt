package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineNotFoundException
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineRegistry
import net.nemerosa.ontrack.extension.config.model.*
import net.nemerosa.ontrack.extension.config.scm.SCMEngine
import net.nemerosa.ontrack.extension.config.scm.SCMEngineNotFoundException
import net.nemerosa.ontrack.extension.config.scm.SCMEngineRegistry
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CIConfigurationServiceImpl(
    private val ciConfigurationParser: CIConfigurationParser,
    private val ciEngineRegistry: CIEngineRegistry,
    private val scmEngineRegistry: SCMEngineRegistry,
    private val coreConfigurationService: CoreConfigurationService,
) : CIConfigurationService {

    override fun configureBuild(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>
    ): Build {
        // Parsing of the configuration YAML
        val configuration = ciConfigurationParser.parseConfig(yaml = config)
        // Getting the CI engine from the configuration
        val ciEngine = findCIEngine(ci, configuration)
        // Getting the SCM engine from the configuration
        val scmEngine = findSCMEngine(scm, configuration)
        // Converting the environment into a map
        val env = env.associate { it.name to it.value }

        // Consolidation of the configurations
        val projectConfiguration = consolidateProjectConfiguration(
            input = configuration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
        val branchConfiguration = consolidateBranchConfiguration(
            input = configuration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
        val buildConfiguration = consolidateBuildConfiguration(
            input = configuration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )

        // Launching the project configuration
        val project = coreConfigurationService.configureProject(
            input = configuration,
            configuration = projectConfiguration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
        // Launching the branch configuration
        val branch = coreConfigurationService.configureBranch(
            project = project,
            input = configuration,
            configuration = branchConfiguration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
        // Launching the build configuration
        return coreConfigurationService.configureBuild(
            branch = branch,
            input = configuration,
            configuration = buildConfiguration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
    }

    private fun consolidateProjectConfiguration(
        input: ConfigurationInput,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): ProjectConfiguration {
        // TODO Use the conditions
        return input.configuration.defaults.project
    }

    private fun consolidateBranchConfiguration(
        input: ConfigurationInput,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): BranchConfiguration {
        // TODO Use the conditions
        return input.configuration.defaults.branch
    }

    private fun consolidateBuildConfiguration(
        input: ConfigurationInput,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): BuildConfiguration {
        // TODO Use the conditions
        return input.configuration.defaults.build
    }

    private fun findSCMEngine(
        scm: String?,
        configuration: ConfigurationInput
    ): SCMEngine =
        if (scm.isNullOrBlank()) {
            TODO("Getting the SCM engine from the configuration")
        } else {
            scmEngineRegistry.findSCMEngine(scm)
                ?: throw SCMEngineNotFoundException(scm)
        }

    private fun findCIEngine(
        ci: String?,
        configuration: ConfigurationInput
    ) =
        if (ci.isNullOrBlank()) {
            TODO("Getting the CI engine from the configuration")
        } else {
            ciEngineRegistry.findCIEngine(ci)
                ?: throw CIEngineNotFoundException(ci)
        }

}