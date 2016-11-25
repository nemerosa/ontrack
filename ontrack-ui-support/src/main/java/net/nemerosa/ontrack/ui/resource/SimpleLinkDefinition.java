package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiFunction;

@Data
public class SimpleLinkDefinition<T extends ProjectEntity> implements LinkDefinition<T> {
    private final String name;
    private final BiFunction<T, ResourceContext, Object> linkFn;
    private final BiFunction<T, ResourceContext, Boolean> checkFn;

    @Override
    public LinksBuilder addLink(LinksBuilder linksBuilder, T resource, ResourceContext resourceContext) {
        return linksBuilder.link(
                name,
                linkFn.apply(resource, resourceContext)
        );
    }

}
