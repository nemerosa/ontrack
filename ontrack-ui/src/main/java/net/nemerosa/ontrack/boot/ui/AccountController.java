package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Email;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountInput;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Management of accounts
 */
@RestController
@RequestMapping("/accounts")
public class AccountController extends AbstractResourceController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * List of accounts
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resources<Account> getAccounts() {
        return Resources.of(
                accountService.getAccounts(),
                uri(on(getClass()).getAccounts())
        )
                .with(Link.CREATE, uri(on(AccountController.class).getCreationForm()));
    }

    /**
     * Form to create a built-in account
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public Form getCreationForm() {
        return Form.create()
                .with(Form.defaultNameField())
                .with(Text.of("fullName").length(100).label("Full name").help("Display name for the account"))
                .with(Email.of("email").label("Email").length(200).help("Contact email for the account"))
                .with(Password.of("password").label("Password").length(40).help("Password for the account"));
    }

    /**
     * Creation of a built-in account
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Account create(@RequestBody @Valid AccountInput input) {
        return accountService.create(input);
    }

    /**
     * Gets an account by its ID
     */
    @RequestMapping(value = "{accountId}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable ID accountId) {
        return accountService.getAccount(accountId);
    }

    /**
     * Update form for an account
     */
    @RequestMapping(value = "{accountId}/update", method = RequestMethod.GET)
    public Form getUpdateForm(@PathVariable ID accountId) {
        Account account = accountService.getAccount(accountId);
        Form form = getCreationForm();
        // Name in read-only mode for the default admin
        if (account.isDefaultAdmin()) {
            form = form.with(Form.defaultNameField().readOnly());
        }
        // Password not filled in, and not required on update
        form = form.with(Password.of("password").label("Password").length(40).help("Password for the account. Leave blank to keep it unchanged.").optional());
        // OK
        return form
                .fill("name", account.getName())
                .fill("fullName", account.getFullName())
                .fill("email", account.getEmail())
                ;
    }

    /**
     * Updating an account
     */
    @RequestMapping(value = "{accountId}/update", method = RequestMethod.PUT)
    public Account updateAccount(@PathVariable ID accountId, @RequestBody @Valid AccountInput input) {
        return accountService.updateAccount(accountId, input);
    }

    /**
     * Deleting an account
     */
    @RequestMapping(value = "{accountId}", method = RequestMethod.DELETE)
    public Ack deleteAccount(@PathVariable ID accountId) {
        return accountService.deleteAccount(accountId);
    }

    /**
     * List of groups
     */
    @RequestMapping(value = "groups", method = RequestMethod.GET)
    public Resources<AccountGroup> getAccountGroups() {
        return Resources.of(
                accountService.getAccountGroups(),
                uri(on(getClass()).getAccountGroups())
        )
                .with(Link.CREATE, uri(on(AccountController.class).getGroupCreationForm()));
    }

    /**
     * Form to create an account group
     */
    @RequestMapping(value = "groups/create", method = RequestMethod.GET)
    public Form getGroupCreationForm() {
        return Form.nameAndDescription();
    }

    /**
     * Creation of an account group
     */
    @RequestMapping(value = "groups/create", method = RequestMethod.POST)
    public AccountGroup create(@RequestBody @Valid NameDescription nameDescription) {
        return accountService.createGroup(nameDescription);
    }

    /**
     * Getting a group
     */
    @RequestMapping(value = "groups/{groupId}", method = RequestMethod.GET)
    public AccountGroup getGroup(@PathVariable ID groupId) {
        return accountService.getAccountGroup(groupId);
    }

    /**
     * Form to update an account group
     */
    @RequestMapping(value = "groups/{groupId}/update", method = RequestMethod.GET)
    public Form getGroupUpdateForm(@PathVariable ID groupId) {
        AccountGroup group = accountService.getAccountGroup(groupId);
        return getGroupCreationForm()
                .fill("name", group.getName())
                .fill("description", group.getDescription());
    }

    /**
     * Updating a group
     */
    @RequestMapping(value = "groups/{groupId}/update", method = RequestMethod.PUT)
    public AccountGroup updateGroup(@PathVariable ID groupId, @RequestBody @Valid NameDescription input) {
        return accountService.updateGroup(groupId, input);

    }

    /**
     * Deleting a group. This does not delete the associated accounts, only the links to them.
     */
    @RequestMapping(value = "groups/{groupId}", method = RequestMethod.DELETE)
    public Ack deleteGroup(@PathVariable ID groupId) {
        return accountService.deleteGroup(groupId);

    }

    // TODO Group deletion

}
