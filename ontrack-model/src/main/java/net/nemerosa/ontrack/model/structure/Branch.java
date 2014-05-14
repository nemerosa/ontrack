package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class Branch {

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({PromotionView.class, Branch.class})
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

}
