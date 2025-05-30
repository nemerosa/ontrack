package net.nemerosa.ontrack.extension.license.remote.stripe

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForObject

@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "stripe",
    matchIfMissing = false
)
class StripeLicenseService(
    private val configurationProperties: StripeLicenseConfigurationProperties,
) : LicenseService, JobProvider, StartupService {

    private val logger: Logger = LoggerFactory.getLogger(StripeLicenseService::class.java)

    private var stripeLicense: License? = null

    private fun collectStripeLicense() {
        val token = configurationProperties.token ?: error("Stripe API token is not configured.")
        val subscriptionId = configurationProperties.subscription ?: error("Stripe Subscription ID is not configured.")

        val template = RestTemplateBuilder()
            .rootUri("https://api.stripe.com")
            .defaultHeader("Authorization", "Bearer $token")
            .build()

        val subscription = template.getForObject<StripeSubscription>("/v1/subscriptions/$subscriptionId")
        val customer = template.getForObject<StripeCustomer>("/v1/customers/${subscription.customer}")

        val information = StripeInformation(customer, subscription)

        val license = information.extractLicense()

        logger.info("Stripe license: $license")

        stripeLicense = license
    }

    override val license: License
        get() = stripeLicense!!

    override fun getStartingJobs(): Collection<JobRegistration> =
        listOf(
            JobRegistration(
                createStripeLicenseJob(),
                Schedule.EVERY_DAY
            )
        )

    private fun createStripeLicenseJob() = object : Job {
        override fun getKey(): JobKey =
            JobCategory.of("licensing").withName("Licensing")
                .getType("stripe").withName("Stripe")
                .getKey("collection")

        override fun getTask() = JobRun {
            collectStripeLicense()
        }

        override fun getDescription(): String = "Get the license from Stripe"

        override fun isDisabled(): Boolean = false
    }

    override fun getName(): String = "Stripe licensing"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        collectStripeLicense()
    }
}