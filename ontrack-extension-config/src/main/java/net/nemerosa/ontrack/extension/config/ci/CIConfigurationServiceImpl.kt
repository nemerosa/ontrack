package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineNotFoundException
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngineRegistry
import net.nemerosa.ontrack.extension.config.model.CIEnv
import net.nemerosa.ontrack.extension.config.model.ConfigurationInput
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CIConfigurationServiceImpl(
    private val ciConfigurationParser: CIConfigurationParser,
    private val ciEngineRegistry: CIEngineRegistry,
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
        TODO("Getting the SCM engine from the configuration")
        TODO("Converting the environment into a map")
        TODO("Launching the project configuration")
        TODO("Launching the branch configuration")
        TODO("Launching the build configuration")
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