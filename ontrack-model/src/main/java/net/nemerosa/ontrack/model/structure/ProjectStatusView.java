package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

@Data
public class ProjectStatusView implements View {

    /**
     * The project
     */
    private final Project project;

    /**
     * Its decorations
     */
    private final List<Decoration<?>> decorations;

    /**
     * Its branches
     */
    private final List<BranchStatusView> branchStatusViews;

}
