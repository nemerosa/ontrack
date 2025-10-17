package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.ci.conditions.ConditionRegistry
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineNotDetectedException
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineNotFoundException
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineRegistry
import net.nemerosa.ontrack.extension.config.model.*
import net.nemerosa.ontrack.extension.config.scm.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
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

    override fun effectiveCIConfiguration(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>
    ): EffectiveConfiguration {
        val context = getConfigContext(
            yaml = config,
            ci = ci,
            scm = scm,
            env = env,
        )
        val projectConfiguration = consolidateProjectConfiguration(
            input = context.configurationInput,
            customConfigs = context.customConfigs,
        )
        val branchConfiguration = consolidateBranchConfiguration(
            input = context.configurationInput,
            customConfigs = context.customConfigs,
        )
        val buildConfiguration = consolidateBuildConfiguration(
            input = context.configurationInput,
            customConfigs = context.customConfigs,
        )
        return EffectiveConfiguration(
            configuration = Configuration(
                project = projectConfiguration,
                branch = branchConfiguration,
                build = buildConfiguration,
            ),
            ciEngine = context.ciEngine.name,
            scmEngine = context.scmEngine.name,
        )
    }

    override fun configureProject(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>
    ): Project {
        val context = getConfigContext(
            yaml = config,
            ci = ci,
            scm = scm,
            env = env,
        )
        return configureProjectWithContext(context)
    }

    private fun configureProjectWithContext(context: ConfigContext): Project {
        // Consolidation of the configurations
        val projectConfiguration = consolidateProjectConfiguration(
            input = context.configurationInput,
            customConfigs = context.customConfigs,
        )

        // Launching the project configuration
        return coreConfigurationService.configureProject(
            input = context.configurationInput,
            configuration = projectConfiguration,
            ciEngine = context.ciEngine,
            scmEngine = context.scmEngine,
            env = context.env,
        )
    }

    override fun configureBranch(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>
    ): Branch {
        val context = getConfigContext(
            yaml = config,
            ci = ci,
            scm = scm,
            env = env,
        )
        return configureBranchWithContext(context)
    }

    private fun configureBranchWithContext(context: ConfigContext): Branch {
        val project = configureProjectWithContext(context)

        val branchConfiguration = consolidateBranchConfiguration(
            input = context.configurationInput,
            customConfigs = context.customConfigs,
        )

        return coreConfigurationService.configureBranch(
            project = project,
            input = context.configurationInput,
            configuration = branchConfiguration,
            ciEngine = context.ciEngine,
            scmEngine = context.scmEngine,
            env = context.env,
        )
    }

    override fun configureBuild(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>
    ): Build {
        val context = getConfigContext(
            yaml = config,
            ci = ci,
            scm = scm,
            env = env,
        )
        return configureBuildWithContext(context)
    }

    private fun configureBuildWithContext(context: ConfigContext): Build {
        val branch = configureBranchWithContext(context)
        val buildConfiguration = consolidateBuildConfiguration(
            input = context.configurationInput,
            customConfigs = context.customConfigs,
        )
        return coreConfigurationService.configureBuild(
            branch = branch,
            input = context.configurationInput,
            configuration = buildConfiguration,
            ciEngine = context.ciEngine,
            scmEngine = context.scmEngine,
            env = context.env,
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
        ciEngine: CIEngine,
        env: Map<String, String>,
    ): SCMEngine {
        if (scm.isNullOrBlank()) {
            val scmUrl = ciEngine.getScmUrl(env) ?: throw SCMEngineNoURLException()
            return scmEngineRegistry.engines.find {
                it.matchesUrl(scmUrl)
            } ?: throw SCMEngineNotDetectedException()
        } else {
            return scmEngineRegistry.findSCMEngine(scm)
                ?: throw SCMEngineNotFoundException(scm)
        }
    }

    private fun findCIEngine(
        ci: String?,
        env: Map<String, String>,
    ) =
        if (ci.isNullOrBlank()) {
            ciEngineRegistry.engines.find {
                it.matchesEnv(env)
            } ?: throw CIEngineNotDetectedException()
        } else {
            ciEngineRegistry.findCIEngine(ci)
                ?: throw CIEngineNotFoundException(ci)
        }

    private data class ConfigContext(
        val configurationInput: ConfigurationInput,
        val env: Map<String, String>,
        val ciEngine: CIEngine,
        val scmEngine: SCMEngine,
        val customConfigs: List<CustomConfig>,
    )

    private fun getConfigContext(
        yaml: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>,
    ): ConfigContext {
        // Parsing of the configuration YAML
        val configurationInput = ciConfigurationParser.parseConfig(yaml = yaml)
        // Converting the environment into a map
        val env = env.associate { it.name to it.value }
        // Getting the CI engine from the configuration
        val ciEngine = findCIEngine(ci, env)
        // Getting the SCM engine from the configuration
        val scmEngine = findSCMEngine(scm, ciEngine, env)

        // Getting the custom configurations which match the current environment
        val customConfigs = matchingConfigs(configurationInput, ciEngine, env)

        // OK
        return ConfigContext(
            configurationInput = configurationInput,
            env = env,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            customConfigs = customConfigs,
        )
    }

}