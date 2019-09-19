package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * This {@link URIBuilder} builds URI relative to the site root (the returned URI start with a <code>/</code>).
 */
@Component
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
@ConditionalOnProperty(prefix = "ontrack.config", name = "uri", havingValue = "relative", matchIfMissing = true)
public class RelativeURIBuilder extends AbstractURIBuilder {

    @Override
    public URI build(Object methodInvocation) {

        // Default builder
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(
                UriComponentsBuilder.newInstance(),
                methodInvocation
        );

        // OK
        return builder.build().toUri();
    }

    @Override
    public URI url(String relativeUri) {
        return URI.create(relativeUri);
    }

    @Override
    public URI page(String path, Object... arguments) {
        String pagePath = pagePath(path, arguments);
        return URI.create(pagePath);
    }
}
