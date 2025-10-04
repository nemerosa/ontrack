package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineNotFoundException
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineRegistry
import net.nemerosa.ontrack.extension.config.model.CIEnv
import net.nemerosa.ontrack.extension.config.model.ConfigurationInput
import net.nemerosa.ontrack.extension.config.model.CoreConfigurationService
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

        // Launching the project configuration
        val project = coreConfigurationService.configureProject(
            configuration = configuration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
        // Launching the branch configuration
        val branch = coreConfigurationService.configureBranch(
            project = project,
            configuration = configuration,
            ciEngine = ciEngine,
            scmEngine = scmEngine,
            env = env,
        )
        TODO("Launching the build configuration")
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