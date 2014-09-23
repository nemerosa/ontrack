package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
public class Build implements ProjectEntity {

    private final ID id;
    private final String name;
    private final String description;
    private final Signature signature;
    @JsonView({Build.class, PromotionRun.class, ValidationRun.class})
    private final Branch branch;

    public static Build of(Branch branch, NameDescription nameDescription, Signature signature) {
        return new Build(
                ID.NONE,
                nameDescription.getName(),
                nameDescription.getDescription(),
                signature,
                branch
        );
    }

    @Override
    public Project getProject() {
        return getBranch().getProject();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.BUILD;
    }

    public Build withId(ID id) {
        return new Build(id, name, description, signature, branch);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }

    public Form asForm() {
        return form()
                .fill("name", name)
                .fill("description", description);
    }

    public Build update(NameDescription nameDescription) {
        return new Build(
                id,
                nameDescription.getName(),
                nameDescription.getDescription(),
                signature,
                branch
        );
    }
}
