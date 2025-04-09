package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.AccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AccountProvisioningStartup(
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val accountRepository: AccountRepository,
    private val accountGroupRepository: AccountGroupRepository,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(AccountProvisioningStartup::class.java)

    override fun getName(): String = "Admin account provisioning"

    override fun startupOrder(): Int = StartupService.SYSTEM + 1

    override fun start() {
        if (ontrackConfigProperties.authorization.provisioning) {
            val admin = ontrackConfigProperties.authorization.admin
            logger.info("[account provisioning] Admin email     : ${admin.email}")
            logger.info("[account provisioning] Admin full name : ${admin.fullName}")
            logger.info("[account provisioning] Admin group name: ${admin.groupName}")
            // Checks the existing account
            val existing = accountRepository.findAccountByName(admin.email)
            if (existing != null) {
                logger.info("[account provisioning] Admin username already existing, not touching")
            } else {
                val accountDef = Account(
                    id = ID.NONE,
                    name = admin.email,
                    fullName = admin.fullName,
                    email = admin.email,
                    role = SecurityRole.ADMINISTRATOR,
                )
                val account = accountRepository.newAccount(accountDef)
                logger.info("[account provisioning] Admin user created with ID = ${account.id}")
                // Linking to the group
                if (admin.groupName.isNotBlank()) {
                    logger.info("[account provisioning] Linking admin user to group ${admin.groupName}")
                    val group = accountGroupRepository.findAccountGroupByName(admin.groupName)
                        ?: error("Could not find group with name = ${admin.groupName}")
                    accountGroupRepository.linkAccountToGroups(
                        account.id(),
                        listOf(group.id())
                    )
                }
            }
        } else {
            logger.info("[account provisioning] Provisioning of admin user not enabled")
        }
    }
}