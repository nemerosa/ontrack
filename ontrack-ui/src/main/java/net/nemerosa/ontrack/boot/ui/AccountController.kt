package net.nemerosa.ontrack.boot.ui

import jakarta.validation.Valid
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Management of accounts
 */
@RestController
@RequestMapping("/rest/accounts")
class AccountController(
    private val accountService: AccountService
) {
    /**
     * List of accounts
     */
    @GetMapping("")
    fun getAccounts(): ResponseEntity<List<Account>> = ResponseEntity.ok(
        accountService.accounts,
    )

    /**
     * Gets an account by its ID
     */
    @GetMapping("{accountId}")
    fun getAccount(@PathVariable accountId: ID): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.getAccount(accountId))
    }

    /**
     * Updating an account
     */
    @PutMapping("{accountId}/update")
    fun updateAccount(@PathVariable accountId: ID, @RequestBody @Valid input: AccountInput): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.updateAccount(accountId, input))
    }

    /**
     * Deleting an account
     */
    @DeleteMapping("{accountId}")
    fun deleteAccount(@PathVariable accountId: ID): ResponseEntity<Ack> {
        return ResponseEntity.ok(accountService.deleteAccount(accountId))
    }

    /**
     * List of groups
     */
    @GetMapping("groups")
    fun getAccountGroups(): ResponseEntity<List<AccountGroup>> = ResponseEntity.ok(
        accountService.accountGroups,
    )

    /**
     * Creation of an account group
     */
    @PostMapping("groups/create")
    fun create(@RequestBody @Valid input: AccountGroupInput): ResponseEntity<AccountGroup> {
        return ResponseEntity.ok(accountService.createGroup(input))
    }

    /**
     * Getting a group
     */
    @GetMapping("groups/{groupId}")
    fun getGroup(@PathVariable groupId: ID): ResponseEntity<AccountGroup> {
        return ResponseEntity.ok(accountService.getAccountGroup(groupId))
    }

    /**
     * Updating a group
     */
    @PutMapping("groups/{groupId}/update")
    fun updateGroup(
        @PathVariable groupId: ID,
        @RequestBody @Valid input: AccountGroupInput
    ): ResponseEntity<AccountGroup> {
        return ResponseEntity.ok(accountService.updateGroup(groupId, input))
    }

    /**
     * Deleting a group. This does not delete the associated accounts, only the links to them.
     */
    @DeleteMapping("groups/{groupId}")
    fun deleteGroup(@PathVariable groupId: ID): ResponseEntity<Ack> {
        return ResponseEntity.ok(accountService.deleteGroup(groupId))
    }

}