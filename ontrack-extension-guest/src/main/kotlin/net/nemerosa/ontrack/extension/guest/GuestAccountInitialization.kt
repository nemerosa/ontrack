package net.nemerosa.ontrack.extension.guest

import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.BuiltinAuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GuestAccountInitialization(
    private val guestExtensionProperties: GuestExtensionProperties,
    private val accountService: AccountService,
    private val securityService: SecurityService,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(GuestAccountInitialization::class.java)

    override fun getName(): String = "Guest account"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        if (guestExtensionProperties.enabled) {
            setupGuestAccount()
        } else {
            cleanupGuestAccount()
        }
    }

    /**
     * Creates the service account if needed
     */
    private fun setupGuestAccount() {
        securityService.asAdmin {
            val account = accountService.findAccountByName(guestExtensionProperties.username)
            if (account != null) {
                logger.info("Guest account already exists.")
            } else {
                logger.info("Guest account does not exist yet - creating.")
                accountService.create(
                    AccountInput(
                        name = guestExtensionProperties.username,
                        fullName = guestExtensionProperties.fullname,
                        email = "n/a",
                        password = guestExtensionProperties.password,
                        groups = emptyList(),
                    )
                )
            }
        }
    }

    /**
     * Removes any existing guest account
     */
    private fun cleanupGuestAccount() {
        securityService.asAdmin {
            val account = accountService.findAccountByName(guestExtensionProperties.username)
            if (account != null && account.authenticationSource.provider == BuiltinAuthenticationSourceProvider.ID) {
                accountService.deleteAccount(account.id)
            }
        }
    }
}