package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiPredicate;

@Data
public class EntityPageLinkDefinition<T extends ProjectEntity> implements LinkDefinition<T> {

    private final BiPredicate<T, ResourceContext> checkFn;

    @Override
    public String getName() {
        return Link.PAGE;
    }

    @Override
    public LinksBuilder addLink(LinksBuilder linksBuilder, T resource, ResourceContext resourceContext) {
        return linksBuilder.page(resource);
    }

}
