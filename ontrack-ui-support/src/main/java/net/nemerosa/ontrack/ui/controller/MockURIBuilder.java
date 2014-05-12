package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.common.RunProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

import static java.lang.String.format;

@Component
@Profile({RunProfile.UNIT_TEST})
public class MockURIBuilder implements URIBuilder {
    @Override
    public URI build(Object invocationInfo) {
        MvcUriComponentsBuilder.MethodInvocationInfo info = (MvcUriComponentsBuilder.MethodInvocationInfo) invocationInfo;
        return URI.create(
                format(
                        "urn:test:%s#%s:%s",
                        info.getControllerMethod().getDeclaringClass().getName(),
                        info.getControllerMethod().getName(),
                        StringUtils.join(info.getArgumentValues(), ",")
                )
        );
    }
}
