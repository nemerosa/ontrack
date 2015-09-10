package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;

public interface DecorationExtension<T> extends Extension, Decorator<T> {

    /**
     * Scope of the decorator
     *
     * @return List of {@link net.nemerosa.ontrack.model.structure.ProjectEntityType} this decorator can apply to
     */
    EnumSet<ProjectEntityType> getScope();

}
