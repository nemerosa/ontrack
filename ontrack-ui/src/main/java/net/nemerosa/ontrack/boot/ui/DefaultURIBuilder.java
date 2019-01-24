package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.RunProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Component
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
@ConditionalOnProperty(prefix = "ontrack.config", name = "uri", havingValue = "absolute")
public class DefaultURIBuilder extends AbstractURIBuilder {
    @Override
    public URI build(Object methodInvocation) {

        // Default builder
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(methodInvocation);

        // Default URI
        UriComponents uriComponents = builder.build();

        // TODO #251 Workaround for SPR-12771
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        String portHeader = httpRequest.getHeaders().getFirst("X-Forwarded-Port");
        if (StringUtils.hasText(portHeader)) {
            int port = Integer.parseInt(portHeader);
            String scheme = uriComponents.getScheme();
            if (("https".equals(scheme) && port == 443) || ("http".equals(scheme) && port == 80)) {
                port = -1;
            }
            builder.port(port);
        }

        // OK
        return builder.build().toUri();
    }

    @Override
    public URI url(String relativeUri) {
        return URI.create(
                ServletUriComponentsBuilder.fromCurrentServletMapping().build().toUriString() +
                        relativeUri
        );
    }

    @Override
    public URI page(String path, Object... arguments) {
        String pagePath = pagePath(path, arguments);
        return URI.create(
                ServletUriComponentsBuilder.fromCurrentServletMapping().build().toUriString() +
                        pagePath
        );
    }
}
