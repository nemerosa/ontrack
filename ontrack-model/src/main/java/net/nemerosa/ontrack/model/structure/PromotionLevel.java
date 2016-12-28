package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({"id", "name", "description", "branch", "image"})
public class PromotionLevel implements ProjectEntity {

    public static PromotionLevel of(Branch branch, NameDescription nameDescription) {
        Entity.isEntityDefined(branch, "Branch must be defined");
        Entity.isEntityDefined(branch.getProject(), "Project must be defined");
        return new PromotionLevel(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), branch, false,
                Signature.none()
        );
    }

    private final ID id;
    private final String name;
    @Wither
    private final String description;
    @JsonView({PromotionLevel.class, PromotionView.class, PromotionRunView.class})
    private final Branch branch;
    private final Boolean image;
    @Wither
    private final Signature signature;

    @Override
    public Project getProject() {
        return getBranch().getProject();
    }

    @Override
    public ProjectEntityType getProjectEntityType() {
        return ProjectEntityType.PROMOTION_LEVEL;
    }

    @Override
    public String getEntityDisplayName() {
        return String.format("Promotion level %s/%s/%s", branch.getProject().getName(), branch.getName(), name);
    }

    public PromotionLevel withId(ID id) {
        return new PromotionLevel(id, name, description, branch, image, signature);
    }

    public PromotionLevel withImage(boolean image) {
        return new PromotionLevel(id, name, description, branch, image, signature);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }

    public Form asForm() {
        return form()
                .fill("name", name)
                .fill("description", description);
    }

    public PromotionLevel update(NameDescription nameDescription) {
        return new PromotionLevel(
                id,
                nameDescription.getName(),
                nameDescription.getDescription(),
                branch,
                image,
                signature
        );
    }
}
