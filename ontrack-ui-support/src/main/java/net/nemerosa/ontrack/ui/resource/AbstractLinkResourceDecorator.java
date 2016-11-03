package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.common.CachedSupplier;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractLinkResourceDecorator<T extends ProjectEntity> extends AbstractResourceDecorator<T> {

    private final Supplier<Iterable<LinkDefinition<T>>> linkDefinitions;

    protected AbstractLinkResourceDecorator(Class<T> resourceClass) {
        super(resourceClass);
        this.linkDefinitions = CachedSupplier.of(this::getLinkDefinitions);
    }

    @Override
    public List<Link> links(T resource, ResourceContext resourceContext) {
        LinksBuilder linksBuilder = resourceContext.links();
        for (LinkDefinition<T> linkDefinition : linkDefinitions.get()) {
            if (linkDefinition.getCheckFn().apply(resource, resourceContext)) {
                linksBuilder = linkDefinition.addLink(linksBuilder, resource, resourceContext);
            }
        }
        return linksBuilder.build();
    }

    protected abstract Iterable<LinkDefinition<T>> getLinkDefinitions();

    @Override
    public List<String> getLinkNames() {
        List<String> names = new ArrayList<>();
        for (LinkDefinition<?> ld : linkDefinitions.get()) {
            names.add(ld.getName());
        }
        return names;
    }

}
