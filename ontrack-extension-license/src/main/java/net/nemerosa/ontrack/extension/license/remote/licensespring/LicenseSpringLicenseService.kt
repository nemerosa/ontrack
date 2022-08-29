package net.nemerosa.ontrack.extension.license.remote.licensespring

import com.licensespring.management.LicenseService
import com.licensespring.management.ManagementConfiguration
import com.licensespring.management.dto.request.SearchLicensesRequest
import com.licensespring.management.model.BackOfficeCustomer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.LocalDate

typealias OntrackLicense = net.nemerosa.ontrack.extension.license.License
typealias OntrackLicenseService = net.nemerosa.ontrack.extension.license.LicenseService

@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "license-spring",
    matchIfMissing = false
)
class LicenseSpringLicenseService(
    configurationProperties: LicenseSpringLicenseServiceConfigurationProperties,
) : OntrackLicenseService {

    private val key: String
    private val licenseService: LicenseService

    init {
        val mgtKey = configurationProperties.management.key
            ?.takeIf { it.isNotBlank() }
            ?: error("ontrack.config.license.licensespring.management.key is blank or not defined.")
        val configuration = ManagementConfiguration.builder()
            .managementKey(mgtKey)
            .build()
        licenseService = LicenseService(configuration)
        key = configurationProperties.key
            ?.takeIf { it.isNotBlank() }
            ?: error("ontrack.config.license.licensespring.key is blank or not defined.")
    }

    override val license: OntrackLicense?
        get() {
            // TODO Periodic check only, with a job
            val request = SearchLicensesRequest.builder()
                .licenseKey(key)
                .build()
            val result = licenseService.searchLicenses(request)
            val license = result.results.firstOrNull()
            return license?.run {
                OntrackLicense(
                    // TODO Gets the product name
                    name = "FREE PLAN",
                    assignee = getName(license.customer),
                    validUntil = license.validityPeriod
                        ?.takeIf { it.isNotBlank() }
                        ?.let { LocalDate.parse(it) }
                        ?.atTime(23, 59, 0),
                    // TODO Gets the metadata field
                    maxProjects = 0,
                )
            }
        }

    private fun getName(customer: BackOfficeCustomer?): String =
        if (customer != null) {
            when {
                !customer.companyName.isNullOrBlank() -> customer.companyName
                !customer.firstName.isNullOrBlank() && !customer.lastName.isNullOrBlank() -> "${customer.firstName} ${customer.lastName}"
                else -> customer.id.toString()
            }
        } else {
            "n/a"
        }

}
