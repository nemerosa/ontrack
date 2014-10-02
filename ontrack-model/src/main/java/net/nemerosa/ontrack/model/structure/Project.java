package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Project implements ProjectEntity {

    private final ID id;
    private final String name;
    private final String description;
    @Wither
    private final boolean disabled;

    public Project withId(ID id) {
        return new Project(id, name, description, disabled);
    }

    public static Project of(NameDescription nameDescription) {
        return new Project(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), false);
    }

    @Override
    public Project getProject() {
        return this;
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.PROJECT;
    }

    public static Form form() {
        return Form.nameAndDescription();
    }

    public Project update(NameDescription nameDescription) {
        return of(nameDescription).withId(id);
    }

    public Form asForm() {
        return form().name(name).description(description);
    }
}
