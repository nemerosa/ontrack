package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.common.RunProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Component
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
public class DefaultURIBuilder implements URIBuilder {
    @Override
    public URI build(Object methodInvocation) {

        // Default builder
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(methodInvocation);

        // TODO #251 Workaround for SPR-12771
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        String portHeader = httpRequest.getHeaders().getFirst("X-Forwarded-Port");
        if (StringUtils.hasText(portHeader)) {
            int port = Integer.parseInt(portHeader);
            builder.port(port);
        }

        // OK
        return builder.build().toUri();
    }
}
