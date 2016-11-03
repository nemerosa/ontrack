package net.nemerosa.ontrack.ui.resource;

import com.google.common.collect.ImmutableList;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.List;
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

}
