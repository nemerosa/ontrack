package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@Data
public class PagePathLinkDefinition<T extends ProjectEntity> implements LinkDefinition<T> {

    private final String name;
    private final BiFunction<T, ResourceContext, String> pathFn;
    private final BiPredicate<T, ResourceContext> checkFn;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LinksBuilder addLink(LinksBuilder linksBuilder, T resource, ResourceContext resourceContext) {
        return linksBuilder.page(
                name,
                checkFn.test(resource, resourceContext),
                pathFn.apply(resource, resourceContext)
        );
    }

}
