package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

public final class ResourceDecorators {

    public static <T extends ProjectEntity> ResourceDecorator<T> decoratorWithExtension(
            Class<T> entityClass,
            ResourceDecorationContributor<T> contributor
    ) {
        return new AbstractLinkResourceDecorator<T>(entityClass) {
            @Override
            public boolean appliesFor(Class<?> beanClass) {
                return entityClass.isAssignableFrom(beanClass);
            }

            @Override
            protected Iterable<LinkDefinition<T>> getLinkDefinitions() {
                return contributor.getLinkDefinitions();
            }
        };
    }

}
