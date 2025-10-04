package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.extension.config.model.CIEnv
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CIConfigurationServiceImpl(
    private val ciConfigurationParser: CIConfigurationParser,
) : CIConfigurationService {

    override fun configureBuild(
        config: String,
        ci: String?,
        scm: String?,
        env: List<CIEnv>
    ): Build {
        // Parsing of the configuration YAML
        val configuration = ciConfigurationParser.parseConfig(yaml = config)
        TODO("Getting the CI engine from the configuration")
        TODO("Getting the SCM engine from the configuration")
        TODO("Converting the environment into a map")
        TODO("Launching the project configuration")
        TODO("Launching the branch configuration")
        TODO("Launching the build configuration")
    }

}