package net.nemerosa.ontrack.ui.resource;

import com.google.common.collect.ImmutableList;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractLinkResourceDecorator<T extends ProjectEntity> extends AbstractResourceDecorator<T> {

    private final List<LinkDefinition<T>> linkDefinitions;

    protected AbstractLinkResourceDecorator(Class<T> resourceClass) {
        super(resourceClass);
        this.linkDefinitions = ImmutableList.copyOf(getLinkDefinitions());
    }

    @Override
    public List<Link> links(T resource, ResourceContext resourceContext) {
        LinksBuilder linksBuilder = resourceContext.links();
        for (LinkDefinition<T> linkDefinition : linkDefinitions) {
            if (linkDefinition.getCheckFn().apply(resource, resourceContext)) {
                linksBuilder = linkDefinition.addLink(linksBuilder, resource, resourceContext);
            }
        }
        return linksBuilder.build();
    }

    protected abstract Iterable<LinkDefinition<T>> getLinkDefinitions();

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

    protected LinkDefinition<T> link(String name, BiFunction<T, ResourceContext, Object> linkFn,
                                     BiFunction<T, ResourceContext, Boolean> checkFn) {
        return new SimpleLinkDefinition<>(
                name,
                linkFn,
                checkFn
        );
    }

    protected LinkDefinition<T> link(String name, BiFunction<T, ResourceContext, Object> linkFn) {
        return link(
                name,
                linkFn,
                (t, rc) -> true
        );
    }

    protected LinkDefinition<T> link(String name, Function<T, Object> linkFn) {
        return link(
                name,
                (t, resourceContext) -> linkFn.apply(t)
        );
    }

    protected LinkDefinition<T> link(String name, Function<T, Object> linkFn, BiFunction<T, ResourceContext, Boolean> checkFn) {
        return link(
                name,
                (t, resourceContext) -> linkFn.apply(t),
                checkFn
        );
    }

    /**
     * Creation of a link to the entity's page
     */
    protected LinkDefinition page() {
        return new PageLinkDefinition<T>(
                (e, rc) -> true
        );
    }

}
