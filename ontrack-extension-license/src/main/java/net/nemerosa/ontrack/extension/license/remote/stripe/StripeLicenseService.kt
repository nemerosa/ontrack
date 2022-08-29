package net.nemerosa.ontrack.extension.license.remote.stripe

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.job.JobRegistration
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "stripe",
    matchIfMissing = false
)
class StripeLicenseService : LicenseService, JobProvider, StartupService {

    private var stripeLicense: License? = null

    override val license: License?
        get() = stripeLicense

    override fun getStartingJobs(): Collection<JobRegistration> {
        TODO("Not yet implemented")
    }

    override fun getName(): String = "Stripe licensing"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        TODO("Not yet implemented")
    }
}