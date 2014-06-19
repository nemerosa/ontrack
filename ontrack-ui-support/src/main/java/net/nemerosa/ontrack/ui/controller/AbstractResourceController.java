package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.extension.api.ActionExtension;
import net.nemerosa.ontrack.model.security.Action;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

public abstract class AbstractResourceController {

    @Autowired
    private URIBuilder uriBuilder;

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMethodCall(Object)
     */
    protected URI uri(Object methodInvocation) {
        return uriBuilder.build(methodInvocation);
    }

    protected Action resolveExtensionAction(ActionExtension actionExtension) {
        Action action = actionExtension.getAction();
        return action.withUri(
                String.format("extension/%s/%s",
                        actionExtension.getFeature().getId(),
                        action.getUri())
        );
    }

}
