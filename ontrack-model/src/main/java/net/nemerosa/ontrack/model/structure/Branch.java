package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Branch implements ProjectEntity {

    private final ID id;
    private final String name;
    @Wither
    private final String description;
    @Wither
    private final boolean disabled;
    @Wither
    private final BranchType type;
    @JsonView({
            PromotionView.class, Branch.class, Build.class, PromotionLevel.class, ValidationStamp.class,
            PromotionRun.class, ValidationRun.class, PromotionRunView.class
    })
    @JsonProperty("project")
    private final Project project;
    @Wither
    private final Signature signature;

    public static Branch of(Project project, NameDescription nameDescription) {
        return of(project, nameDescription.asState());
    }

    public static Branch of(Project project, NameDescriptionState nameDescription) {
        return new Branch(
                ID.NONE,
                nameDescription.getName(),
                nameDescription.getDescription(),
                nameDescription.isDisabled(),
                BranchType.CLASSIC,
                project,
                Signature.none()
        );
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.BRANCH;
    }

    @Override
    public String getEntityDisplayName() {
        return String.format("Branch %s/%s", project.getName(), name);
    }

    public Branch withId(ID id) {
        return new Branch(id, name, description, disabled, type, project, signature);
    }

    public static Form form() {
        return Form.create()
                .with(
                        Form.defaultNameField().length(120)
                )
                .description()
                .with(
                        YesNo.of("disabled").label("Disabled").help("Check if the branch must be disabled.")
                );
    }

    public Form toForm() {
        return form()
                .fill("name", name)
                .fill("description", description)
                .fill("disabled", disabled);
    }

    public Branch update(NameDescriptionState form) {
        return of(project, form).withId(id).withDisabled(form.isDisabled());
    }
}
