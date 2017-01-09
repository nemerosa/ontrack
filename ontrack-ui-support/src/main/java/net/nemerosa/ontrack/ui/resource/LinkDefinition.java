package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiFunction;

public interface LinkDefinition<T extends ProjectEntity> {

    String getName();

    BiFunction<T, ResourceContext, Boolean> getCheckFn();

    LinksBuilder addLink(LinksBuilder linksBuilder, T resource, ResourceContext resourceContext);

}
