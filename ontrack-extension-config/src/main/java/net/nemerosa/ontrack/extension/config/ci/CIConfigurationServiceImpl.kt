package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.ci.conditions.ConditionRegistry
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
    private val conditionRegistry: ConditionRegistry,
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

        // Getting the custom configurations which match the current environment
        val customConfigs = matchingConfigs(configuration, ciEngine, env)

        // Consolidation of the configurations
        val projectConfiguration = consolidateProjectConfiguration(
            input = configuration,
            customConfigs = customConfigs,
        )
        val branchConfiguration = consolidateBranchConfiguration(
            input = configuration,
            customConfigs = customConfigs,
        )
        val buildConfiguration = consolidateBuildConfiguration(
            input = configuration,
            customConfigs = customConfigs,
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
        customConfigs: List<CustomConfig>,
    ): ProjectConfiguration {
        return customConfigs
            .filter { it.project.isNotEmpty() }
            .fold(input.configuration.defaults.project) { acc, config ->
                acc.merge(config.project)
            }
    }

    private fun consolidateBranchConfiguration(
        input: ConfigurationInput,
        customConfigs: List<CustomConfig>,
    ): BranchConfiguration {
        return customConfigs
            .filter { it.branch.isNotEmpty() }
            .fold(input.configuration.defaults.branch) { acc, config ->
                acc.merge(config.branch)
            }
    }

    private fun consolidateBuildConfiguration(
        input: ConfigurationInput,
        customConfigs: List<CustomConfig>,
    ): BuildConfiguration {
        return customConfigs
            .filter { it.build.isNotEmpty() }
            .fold(input.configuration.defaults.build) { acc, config ->
                acc.merge(config.build)
            }
    }

    private fun matchingConfigs(
        input: ConfigurationInput,
        ciEngine: CIEngine,
        env: Map<String, String>
    ): List<CustomConfig> = input.configuration.custom.configs.filter { customConfig ->
        customConfig.conditions.all {
            matchesCondition(ciEngine, it, env)
        }
    }

    private fun matchesCondition(
        ciEngine: CIEngine,
        conditionConfig: ConditionConfig,
        env: Map<String, String>
    ): Boolean {
        // Gets the condition interface
        val condition = conditionRegistry.getCondition(conditionConfig.name)
        // Checks the condition
        return condition.matches(ciEngine, conditionConfig.config, env)
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