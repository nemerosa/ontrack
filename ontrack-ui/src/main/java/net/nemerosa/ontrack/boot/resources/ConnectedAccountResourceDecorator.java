package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.UserAPIController;
import net.nemerosa.ontrack.model.security.ConnectedAccount;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class ConnectedAccountResourceDecorator extends AbstractResourceDecorator<ConnectedAccount> {

    protected ConnectedAccountResourceDecorator() {
        super(ConnectedAccount.class);
    }

    @Override
    public List<Link> links(ConnectedAccount account, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(UserAPIController.class).getCurrentUser())
                // Login if not logged
                .link("login", on(UserAPIController.class).loginForm(), !account.isLogged())
                // OK
                .build();
    }

}
