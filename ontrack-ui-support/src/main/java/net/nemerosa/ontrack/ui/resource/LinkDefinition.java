package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiPredicate;

public interface LinkDefinition<T extends ProjectEntity> {

    String getName();

    BiPredicate<T, ResourceContext> getCheckFn();

    LinksBuilder addLink(LinksBuilder linksBuilder, T resource, ResourceContext resourceContext);

}
