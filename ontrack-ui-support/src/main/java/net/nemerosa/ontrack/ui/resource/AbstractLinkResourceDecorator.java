package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractLinkResourceDecorator<T extends ProjectEntity> extends AbstractResourceDecorator<T> {

    private final List<LinkDefinition> linkDefinitions;

    protected AbstractLinkResourceDecorator(Class<T> resourceClass) {
        super(resourceClass);
        this.linkDefinitions = getLinkDefinitions();
    }

    @Override
    public List<Link> links(T resource, ResourceContext resourceContext) {
        LinksBuilder linksBuilder = resourceContext.links();
        for (LinkDefinition linkDefinition : linkDefinitions) {
            if (linkDefinition.getCheckFn().apply(resource, resourceContext)) {
                linksBuilder = linksBuilder.link(
                        linkDefinition.getName(),
                        linkDefinition.getLinkFn().apply(resource, resourceContext)
                );
            }
        }
        return linksBuilder.build();
    }

    protected abstract List<LinkDefinition> getLinkDefinitions();

    @Override
    public List<String> getLinkNames() {
        return linkDefinitions.stream()
                .map(LinkDefinition::getName)
                .collect(Collectors.toList());
    }

    protected <P extends ProjectFunction> BiFunction<T, ResourceContext, Boolean> withProjectFn(Class<P> projectFn) {
        return (T e, ResourceContext resourceContext) -> resourceContext.isProjectFunctionGranted(e, projectFn);
    }

    protected <G extends GlobalFunction> BiFunction<T, ResourceContext, Boolean> withGlobalFn(Class<G> globalFn) {
        return (T e, ResourceContext resourceContext) -> resourceContext.isGlobalFunctionGranted(globalFn);
    }

    protected LinkDefinition link(String name, BiFunction<T, ResourceContext, Object> linkFn,
                                  BiFunction<T, ResourceContext, Boolean> checkFn) {
        return new LinkDefinition(
                name,
                linkFn,
                checkFn
        );
    }

    protected LinkDefinition link(String name, BiFunction<T, ResourceContext, Object> linkFn) {
        return link(
                name,
                linkFn,
                (t, rc) -> true
        );
    }

    protected LinkDefinition link(String name, Function<T, Object> linkFn) {
        return link(
                name,
                (t, resourceContext) -> linkFn.apply(t)
        );
    }

    protected LinkDefinition link(String name, Function<T, Object> linkFn, BiFunction<T, ResourceContext, Boolean> checkFn) {
        return link(
                name,
                (t, resourceContext) -> linkFn.apply(t),
                checkFn
        );
    }

    @Data
    protected class LinkDefinition {
        private final String name;
        private final BiFunction<T, ResourceContext, Object> linkFn;
        private final BiFunction<T, ResourceContext, Boolean> checkFn;
    }
}
