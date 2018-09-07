package net.nemerosa.ontrack.boot.ui;

import com.google.common.base.CaseFormat;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
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

import static java.lang.String.format;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
public class DefaultURIBuilder implements URIBuilder {
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
        String pagePath = format(
                "/#/%s",
                format(path, arguments)
        );
        return URI.create(
                ServletUriComponentsBuilder.fromCurrentServletMapping().build().toUriString() +
                        pagePath
        );
    }

    @Override
    public URI getEntityURI(ProjectEntity entity) {
        ProjectEntityType projectEntityType = entity.getProjectEntityType();
        switch (projectEntityType) {
            case PROJECT:
                return build(on(ProjectController.class).getProject(entity.getId()));
            case BRANCH:
                return build(on(BranchController.class).getBranch(entity.getId()));
            case PROMOTION_LEVEL:
                return build(on(PromotionLevelController.class).getPromotionLevel(entity.getId()));
            case VALIDATION_STAMP:
                return build(on(ValidationStampController.class).getValidationStamp(entity.getId()));
            case BUILD:
                return build(on(BuildController.class).getBuild(entity.getId()));
            case PROMOTION_RUN:
                return build(on(PromotionRunController.class).getPromotionRun(entity.getId()));
            case VALIDATION_RUN:
                return build(on(ValidationRunController.class).getValidationRun(entity.getId()));
            default:
                throw new IllegalStateException("Unknown entity type: " + projectEntityType);
        }
    }

    String getEntityPageName(ProjectEntityType projectEntityType) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, projectEntityType.name().toLowerCase());
    }

    @Override
    public URI getEntityPage(ProjectEntity entity) {
        return page(
                "%s/%d",
                getEntityPageName(entity.getProjectEntityType()),
                entity.id()
        );
    }
}
