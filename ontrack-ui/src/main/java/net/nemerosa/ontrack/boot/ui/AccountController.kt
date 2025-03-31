package net.nemerosa.ontrack.boot.ui

import jakarta.validation.Valid
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

/**
 * Management of accounts
 */
@RestController
@RequestMapping("/rest/accounts")
class AccountController(
        private val accountService: AccountService
) : AbstractResourceController() {
    /**
     * List of accounts
     */
    @GetMapping("")
    fun getAccounts(): Resources<Account> = Resources.of(
            accountService.accounts,
            uri(MvcUriComponentsBuilder.on(javaClass).getAccounts())
    )
            .with(Link.CREATE, uri(MvcUriComponentsBuilder.on(AccountController::class.java).getCreationForm()))

    /**
     * Form to create a built-in account
     */
    @GetMapping("create")
    fun getCreationForm(): Form = create()
            .with(defaultNameField())
            .with(Text.of("fullName").length(100).label("Full name").help("Display name for the account"))
            .with(Email.of("email").label("Email").length(200).help("Contact email for the account"))
            .with(Password.of("password").label("Password").length(40).help("Password for the account"))
            .with(
                    MultiSelection.of("groups").label("Groups")
                            .items(accountService.getAccountGroupsForSelection(ID.NONE))
            )
            .with(
                YesNo.of(Account::disabled.name)
                    .label("Disabled")
                    .help("Is this account disabled? If disabled, it cannot be used to access Ontrack any longer.")
                    .value(false)
            )
            .with(
                YesNo.of(Account::locked.name)
                    .label("Locked")
                    .help("Is this account locked? If locked, it can be updated only by an administrator.")
                    .value(false)
            )

    /**
     * Creation of a built-in account
     */
    @PostMapping("create")
    fun create(@RequestBody @Valid input: AccountInput): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.create(input))
    }

    /**
     * Gets an account by its ID
     */
    @GetMapping("{accountId}")
    fun getAccount(@PathVariable accountId: ID): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.getAccount(accountId))
    }

    /**
     * Update form for an account
     */
    @GetMapping("{accountId}/update")
    fun getUpdateForm(@PathVariable accountId: ID): Form {
        val account = accountService.getAccount(accountId)
        var form = getCreationForm()
        // Name in read-only mode for the default admin
        if (account.isDefaultAdmin) {
            form = form.with(defaultNameField().readOnly())
        }
        // Password not filled in, and not required on update
        form = form.with(Password.of("password").label("Password").length(40).help("Password for the account. Leave blank to keep it unchanged.").optional())
        // Groups
        form = form.with(
                MultiSelection.of("groups").label("Groups")
                        .items(accountService.getAccountGroupsForSelection(accountId))
        )
        // OK
        return form
                .fill("name", account.name)
                .fill("fullName", account.fullName)
                .fill("email", account.email)
                .fill(Account::disabled.name, account.disabled)
                .fill(Account::locked.name, account.locked)
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
    fun getAccountGroups(): Resources<AccountGroup> = Resources.of(
            accountService.accountGroups,
            uri(MvcUriComponentsBuilder.on(javaClass).getAccountGroups())
    )
            .with(Link.CREATE, uri(MvcUriComponentsBuilder.on(AccountController::class.java).getGroupCreationForm()))

    /**
     * Form to create an account group
     */
    @GetMapping("groups/create")
    fun getGroupCreationForm(): Form = create()
            .name()
            .description()

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
     * Form to update an account group
     */
    @GetMapping("groups/{groupId}/update")
    fun getGroupUpdateForm(@PathVariable groupId: ID): Form {
        val accountGroup = accountService.getAccountGroup(groupId)
        return getGroupCreationForm()
                .fill("name", accountGroup.name)
                .fill("description", accountGroup.description)
    }

    /**
     * Updating a group
     */
    @PutMapping("groups/{groupId}/update")
    fun updateGroup(@PathVariable groupId: ID, @RequestBody @Valid input: AccountGroupInput): ResponseEntity<AccountGroup> {
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