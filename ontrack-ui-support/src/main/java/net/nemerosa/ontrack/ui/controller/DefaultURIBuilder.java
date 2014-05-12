package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.common.RunProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@Component
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
public class DefaultURIBuilder implements URIBuilder {
    @Override
    public URI build(Object methodInvocation) {
        return MvcUriComponentsBuilder.fromMethodCall(methodInvocation).build().toUri();
    }
}
