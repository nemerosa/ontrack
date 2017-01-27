package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Project implements ProjectEntity {

    private final ID id;
    private final String name;
    private final String description;
    @Wither
    private final boolean disabled;
    @Wither
    private final Signature signature;

    public Project withId(ID id) {
        return new Project(id, name, description, disabled, signature);
    }

    public static Project of(NameDescriptionState nameDescription) {
        return new Project(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), nameDescription.isDisabled(),
                Signature.none());
    }

    public static Project of(NameDescription nameDescription) {
        return new Project(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), false,
                Signature.none());
    }

    @Override
    public Project getProject() {
        return this;
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.PROJECT;
    }

    @Override
    public String getEntityDisplayName() {
        return String.format("Project %s", name);
    }

    public static Form form() {
        return Form.nameAndDescription()
                .with(
                        YesNo.of("disabled").label("Disabled").help("Check if the project must be disabled.")
                );
    }

    public Project update(NameDescriptionState form) {
        return of(form).withId(id).withDisabled(form.isDisabled());
    }

    public Form asForm() {
        return form().name(name).description(description).fill("disabled", disabled);
    }
}
