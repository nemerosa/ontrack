package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.RunProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
@ConditionalOnProperty(prefix = "ontrack.config", name = "uri", havingValue = "absolute")
public class DefaultURIBuilder extends AbstractURIBuilder {
    @Override
    public URI build(Object methodInvocation) {

        // Default builder
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(methodInvocation);

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
