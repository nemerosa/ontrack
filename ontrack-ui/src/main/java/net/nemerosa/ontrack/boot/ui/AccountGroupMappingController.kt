package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST API to create & delete group mappings
 */
@RestController
@RequestMapping("/rest/group-mappings")
class AccountGroupMappingController(
        private val accountGroupMappingService: AccountGroupMappingService,
        private val providedGroupsService: ProvidedGroupsService,
        private val authenticationSourceRepository: AuthenticationSourceRepository
) : AbstractResourceController() {

    /**
     * Creates a mapping
     */
    @PostMapping("{provider}/{source}")
    fun createMapping(@PathVariable provider: String, @PathVariable source: String, @RequestBody input: AccountGroupMappingInput): ResponseEntity<AccountGroupMapping> {
        val authenticationSource = authenticationSourceRepository.getRequiredAuthenticationSource(provider, source)
        return ResponseEntity.ok(accountGroupMappingService.newMapping(authenticationSource, input))
    }

    /**
     * Deletes a mapping
     */
    @DeleteMapping("{provider}/{source}/{id}")
    fun deleteMapping(@PathVariable provider: String, @PathVariable source: String, @PathVariable id: ID): ResponseEntity<Ack> {
        val authenticationSource = authenticationSourceRepository.getRequiredAuthenticationSource(provider, source)
        return ResponseEntity.ok(accountGroupMappingService.deleteMapping(authenticationSource, id))
    }

    /**
     * Gets a list of provided groups for a type and token.
     */
    @GetMapping("{provider}/{source}/search/{token:.*}")
    fun getSuggestedMappings(@PathVariable provider: String, @PathVariable source: String, @PathVariable token: String): ResponseEntity<List<String>> {
        val authenticationSource = authenticationSourceRepository.getRequiredAuthenticationSource(provider, source)
        return ResponseEntity.ok(
                providedGroupsService.getSuggestedGroups(authenticationSource, token)
        )
    }

}