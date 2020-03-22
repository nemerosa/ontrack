package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.common.CachedSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractLinkResourceDecorator<T> extends AbstractResourceDecorator<T> {

    private final Supplier<Iterable<LinkDefinition<T>>> linkDefinitions;

    protected AbstractLinkResourceDecorator(Class<T> resourceClass) {
        super(resourceClass);
        this.linkDefinitions = CachedSupplier.of(this::getLinkDefinitions);
    }

    @Override
    public List<Link> links(T resource, ResourceContext resourceContext) {
        LinksBuilder linksBuilder = resourceContext.links();
        for (LinkDefinition<T> linkDefinition : linkDefinitions.get()) {
            if (linkDefinition.getCheckFn().invoke(resource, resourceContext)) {
                linksBuilder = linkDefinition.addLink(linksBuilder, resource, resourceContext);
            }
        }
        return linksBuilder.build();
    }

    @Nullable
    @Override
    public Link linkByName(@NotNull T resource, @NotNull ResourceContext resourceContext, @NotNull String linkName) {
        for (LinkDefinition<T> linkDefinition : linkDefinitions.get()) {
            if (linkName.equals(linkDefinition.getName())) {
                if (linkDefinition.getCheckFn().invoke(resource, resourceContext)) {
                    LinksBuilder linksBuilder = linkDefinition.addLink(resourceContext.links(), resource, resourceContext);
                    List<Link> links = linksBuilder.build();
                    if (links.isEmpty()) {
                        return null;
                    } else {
                        return links.get(0);
                    }
                }
            }
        }
        return null;
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
