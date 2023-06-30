package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.exceptions.CannotCreateWorkingDirException
import net.nemerosa.ontrack.model.structure.VersionInfo
import net.nemerosa.ontrack.model.support.EnvService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import javax.annotation.PostConstruct

@Service
class EnvServiceImpl(
    buildProperties: BuildProperties?,
    configProperties: OntrackConfigProperties,
    ctx: ApplicationContext
) : EnvService {

    private val logger = LoggerFactory.getLogger(EnvService::class.java)

    override val version: VersionInfo by lazy {
        if (buildProperties != null) {
            VersionInfo(
                display = buildProperties.version ?: "n/a",
                full = buildProperties.get("full") ?: "n/a",
                branch = buildProperties.get("branch") ?: "n/a",
                build = buildProperties.get("build") ?: "n/a",
                commit = buildProperties.get("commit") ?: "n/a",
            )
        } else {
            VersionInfo(
                display = "local",
                full = "local",
                branch = "local",
                build = "local",
                commit = "local",
            )
        }
    }

    private val defaultProfiles: String by lazy {
        ctx.environment.defaultProfiles.joinToString(",")
    }

    override val profiles: String by lazy {
        ctx.environment.activeProfiles.joinToString(",")
    }

    private val jdbcUrl: String by lazy {
        ctx.environment.getProperty("spring.datasource.url") ?: "No JDBC URL available"
    }

    private val home: File by lazy {
        File(configProperties.applicationWorkingDir)
    }

    override fun getWorkingDir(context: String, name: String): File {
        val cxd = File(home, context)
        val wd = File(cxd, name)
        try {
            FileUtils.forceMkdir(wd)
        } catch (e: IOException) {
            throw CannotCreateWorkingDirException(wd, e)
        }
        return wd
    }

    @PostConstruct
    fun init() {
        logger.info("[env] With JDK:              {}", System.getProperty("java.version"))
        logger.info("[env] With default profiles: {}", defaultProfiles)
        logger.info("[env] With active profiles:  {}", profiles)
        logger.info("[datasource] URL:            {}", jdbcUrl)
        logger.info("[version] Display:           {}", version.display)
        logger.info("[version] Full:              {}", version.full)
        logger.info("[version] Branch:            {}", version.branch)
        logger.info("[version] Build:             {}", version.build)
        logger.info("[version] Commit:            {}", version.commit)
    }
}
