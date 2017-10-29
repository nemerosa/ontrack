package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
                        Arrays.stream(info.getArgumentValues())
                                .map(o -> Objects.toString(o, ""))
                                .map(this::encode)
                                .collect(Collectors.joining(","))
                )
        );
    }

    @Override
    public URI url(String relativeUri) {
        return URI.create(
                format(
                        "urn:test:%s",
                        relativeUri
                )
        );
    }

    @Override
    public URI page(String path, Object... arguments) {
        return URI.create(
                format(
                        "urn:test:#:%s",
                        format(path, arguments)
                )
        );
    }

    @Override
    public URI getEntityURI(ProjectEntity entity) {
        return URI.create(
                format(
                        "urn:test:entity:%s:%d",
                        entity.getProjectEntityType().name(),
                        entity.id()
                )
        );
    }

    @Override
    public URI getEntityPage(ProjectEntity entity) {
        return page("entity:%s:%d", entity.getProjectEntityType().name(), entity.id());
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot encode URI parameter", e);
        }
    }
}
