package net.nemerosa.ontrack.extension.license.remote.generic

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "generic",
    matchIfMissing = false
)
class GenericRemoteLicenseService(
    private val genericRemoteLicenseConfigurationProperties: GenericRemoteLicenseConfigurationProperties,
) : LicenseService, JobProvider, StartupService {

    private var storedLicense: License? = null

    private fun collectLicense() {
        val url = genericRemoteLicenseConfigurationProperties.url
            ?: error("URL must be configured for the generic remote licensing.")
        val username = genericRemoteLicenseConfigurationProperties.username
        val password = genericRemoteLicenseConfigurationProperties.password
        val template = RestTemplateBuilder()
            .rootUri(url)
            .run {
                if (username != null && password != null) {
                    basicAuthentication(username, password)
                } else {
                    this
                }
            }
            .build()
        val generic = template.getForObject("/", GenericLicense::class.java)
        storedLicense = generic?.run {
            License(
                name = name,
                assignee = assignee,
                validUntil = validUntil,
                maxProjects = maxProjects,
            )
        }
    }

    override val license: License?
        get() = storedLicense

    override fun getStartingJobs(): Collection<JobRegistration> = listOf(
        JobRegistration(
            createLicenseJob(),
            Schedule.EVERY_DAY
        )
    )

    private fun createLicenseJob() = object : Job {

        override fun getKey(): JobKey = JobCategory.of("license").withName("Licensing")
            .getType("remote").withName("Remoting")
            .getKey("collection")

        override fun getTask() = JobRun {
            collectLicense()
        }

        override fun getDescription(): String = "Get the license from a remote license server"

        override fun isDisabled(): Boolean = false
    }

    override fun getName(): String = "License remote collection"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        collectLicense()
    }

}