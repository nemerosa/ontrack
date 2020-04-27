package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.AccountGroupMappingInput
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
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
        private val accountGroupMappingService: AccountGroupMappingService
) : AbstractResourceController() {

    /**
     * Creates a mapping
     */
    @PostMapping("{type}")
    fun createMapping(@PathVariable type: String, @RequestBody input: AccountGroupMappingInput) =
            ResponseEntity.ok(accountGroupMappingService.newMapping(type, input))

    /**
     * Deletes a mapping
     */
    @DeleteMapping("{type}/{id}")
    fun deleteMapping(@PathVariable type: String, @PathVariable id: ID) =
            ResponseEntity.ok(accountGroupMappingService.deleteMapping(type, id))

}