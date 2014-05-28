package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
public class Branch implements Entity {

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({
            PromotionView.class, Branch.class, Build.class, PromotionLevel.class, ValidationStamp.class,
            PromotionRun.class, ValidationRun.class
    })
    private final Project project;

    public static Branch of(Project project, NameDescription nameDescription) {
        return new Branch(
                ID.NONE,
                nameDescription.getName(),
                nameDescription.getDescription(),
                project
        );
    }

    public Branch withId(ID id) {
        return new Branch(id, name, description, project);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }
}
