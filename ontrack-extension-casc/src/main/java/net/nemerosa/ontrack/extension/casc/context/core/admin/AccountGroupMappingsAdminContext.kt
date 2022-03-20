package net.nemerosa.ontrack.extension.casc.context.core.admin

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascArray
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.AuthenticationSourceRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AccountGroupMappingsAdminContext(
    private val accountGroupMappingService: AccountGroupMappingService,
    private val authenticationSourceRepository: AuthenticationSourceRepository,
    private val accountService: AccountService,
) : AbstractCascContext(), SubAdminContext {

    private val logger: Logger = LoggerFactory.getLogger(AccountGroupMappingsAdminContext::class.java)

    override val field: String = "group-mappings"

    override val type: CascType
        get() = cascArray(
            "List of group mappings",
            cascObject(CascMapping::class)
        )

    override fun run(node: JsonNode, paths: List<String>) {
        // Items to provision
        val items = node.mapIndexed { index, child ->
            try {
                child.parse<CascMapping>()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${CascMapping::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }
        // Existing items
        val existing = existingMappings()
        // Synchronizing, preserving the existing groups
        syncForward(
            from = items,
            to = existing
        ) {
            equality { a, b -> a == b }
            onCreation { item ->
                logger.info("Creating account group mapping: $item")
                createGroupMapping(item)
            }
            onModification { item, existing ->
                logger.info("Updating account group mapping: $item --> $existing")
                updateGroupMapping(item)
            }
            onDeletion { existing ->
                logger.info("Preserving existing group mapping: ${existing}")
            }
        }
    }

    private fun updateGroupMapping(item: CascMapping) {
        // Deletes any existing mapping
        val existing = accountGroupMappingService.mappings.firstOrNull {
            CascMapping(
                provider = it.authenticationSource.provider,
                providerKey = it.authenticationSource.key,
                providerGroup = it.name,
                group = it.group.name,
            ) == item
        }
        if (existing != null) {
            accountGroupMappingService.deleteMapping(
                authenticationSource = existing.authenticationSource,
                id = existing.id,
            )
        }
        // Recreates the mapping
        createGroupMapping(item)
    }

    private fun createGroupMapping(mapping: CascMapping) {
        accountGroupMappingService.newMapping(
            authenticationSource = authenticationSourceRepository.getRequiredAuthenticationSource(
                mapping.provider,
                mapping.providerKey,
            ),
            input = AccountGroupMappingInput(
                name = mapping.providerGroup,
                group = accountService.findAccountGroupByName(mapping.group)
                    ?.id
                    ?: throw IllegalStateException("Cannot find account group with name: ${mapping.group}")
            )
        )
    }

    override fun render(): JsonNode =
        existingMappings().asJson()

    private fun existingMappings() = accountGroupMappingService.mappings.map {
        CascMapping(
            provider = it.authenticationSource.provider,
            providerKey = it.authenticationSource.key,
            providerGroup = it.name,
            group = it.group.name,
        )
    }

    private data class CascMapping(
        @APIDescription("ID of the authentication provider: oidc, ldap, ...")
        val provider: String,
        @APIDescription("Identifier of the exact provider source (name of the OIDC provider, leave blank for LDAP)")
        val providerKey: String,
        @APIDescription("Name of the group in the provider")
        val providerGroup: String,
        @APIDescription("Name of the group in Ontrack")
        val group: String,
    )

}