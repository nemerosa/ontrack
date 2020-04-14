package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.UserController;
import net.nemerosa.ontrack.model.security.ConnectedAccount;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ConnectedAccountResourceDecorator extends AbstractResourceDecorator<ConnectedAccount> {

    public ConnectedAccountResourceDecorator() {
        super(ConnectedAccount.class);
    }

    @Override
    public List<Link> links(ConnectedAccount account, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(UserController.class).getCurrentUser())
                        // Login if not logged
                .link(
                        "login",
                        on(UserController.class).loginForm(),
                        !account.isLogged()
                )
                        // Changing his password allowed for connected users which are built-in
                .link(
                        "_changePassword",
                        on(UserController.class).getChangePasswordForm(),
                        account.isLogged() && account.getAccount().getAuthenticationSource().isAllowingPasswordChange()
                )
                        // OK
                .build();
    }

}
