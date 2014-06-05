package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationStamp implements ProjectEntity {

    public static ValidationStamp of(Branch branch, NameDescription nameDescription) {
        Entity.isEntityDefined(branch, "Branch must be defined");
        Entity.isEntityDefined(branch.getProject(), "Project must be defined");
        return new ValidationStamp(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), branch, null, false);
    }

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({ValidationStamp.class})
    private final Branch branch;
    private final User owner;
    private final Boolean image;

    @Override
    public ID getProjectId() {
        return getBranch().getProjectId();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.VALIDATION_STAMP;
    }

    public ValidationStamp withId(ID id) {
        return new ValidationStamp(id, name, description, branch, owner, image);
    }

    public ValidationStamp withImage(boolean image) {
        return new ValidationStamp(id, name, description, branch, owner, image);
    }

    public static Form form() {
        // TODO User selection
        return Form.nameAndDescription();
    }

}
