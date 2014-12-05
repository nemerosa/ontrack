package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.common.RunProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import static java.lang.String.format;

@Component
@Profile({RunProfile.UNIT_TEST})
public class MockURIBuilder implements URIBuilder {
    @Override
    public URI build(Object invocationInfo) {
        MvcUriComponentsBuilder.MethodInvocationInfo info = (MvcUriComponentsBuilder.MethodInvocationInfo) invocationInfo;
        try {
            return URI.create(
                    format(
                            "urn:test:%s#%s:%s",
                            info.getControllerMethod().getDeclaringClass().getName(),
                            info.getControllerMethod().getName(),
                            URLEncoder.encode(
                                    StringUtils.join(info.getArgumentValues(), ","),
                                    "UTF-8"
                            )
                    )
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot encode URI parameter", e);
        }
    }
}
