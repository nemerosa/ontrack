package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Email;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
