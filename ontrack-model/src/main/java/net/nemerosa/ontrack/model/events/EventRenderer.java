package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.support.NameValue;
import org.jetbrains.annotations.NotNull;

public interface EventRenderer {

    @NotNull String render(@NotNull ProjectEntity projectEntity, @NotNull Event event);

    @NotNull String render(@NotNull String valueKey, @NotNull NameValue value, @NotNull Event event);

    @NotNull String renderLink(@NotNull NameValue text, @NotNull NameValue link, @NotNull Event event);
}
