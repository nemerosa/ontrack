package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiFunction;

@Data
public class PagePathLinkDefinition<T extends ProjectEntity> implements LinkDefinition<T> {

    private final String name;
    private final String path;
    private final BiFunction<T, ResourceContext, Boolean> checkFn;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LinksBuilder addLink(LinksBuilder linksBuilder, T resource, ResourceContext resourceContext) {
        return linksBuilder.page(name, path);
    }

}
