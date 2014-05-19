package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionLevel implements Entity {

    public static PromotionLevel of(Branch branch, NameDescription nameDescription) {
        Entity.isEntityDefined(branch, "Branch must be defined");
        Entity.isEntityDefined(branch.getProject(), "Project must be defined");
        return new PromotionLevel(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), branch);
    }

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({PromotionView.class})
    private final Branch branch;

    public PromotionLevel withId(ID id) {
        return new PromotionLevel(id, name, description, branch);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }
}
