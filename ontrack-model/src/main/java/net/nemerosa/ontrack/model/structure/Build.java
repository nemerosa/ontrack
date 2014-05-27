package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
public class Build implements Entity {

    private final ID id;
    private final String name;
    private final String description;
    private final Signature signature;
    @JsonView({Build.class, PromotionRun.class})
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

    public Build withId(ID id) {
        return new Build(id, name, description, signature, branch);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }
}
