package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

import java.util.Collections;
import java.util.List;

@Data
public class Build implements Entity {

    private final ID id;
    private final String name;
    private final String description;
    private final Signature signature;
    @JsonView({Build.class})
    private final Branch branch;
    @JsonView({Build.class, BranchBuildView.class})
    private final List<PromotionRun> promotionRuns;
    @JsonView({Build.class, BranchBuildView.class})
    private final List<ValidationRun> validationRuns;

    public static Build of(Branch branch, NameDescription nameDescription, Signature signature) {
        return new Build(
                ID.NONE,
                nameDescription.getName(),
                nameDescription.getDescription(),
                signature,
                branch,
                Collections.<PromotionRun>emptyList(),
                Collections.<ValidationRun>emptyList()
        );
    }

    public Build withId(ID id) {
        return new Build(id, name, description, signature, branch, promotionRuns, validationRuns);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }
}
