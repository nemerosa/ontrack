package net.nemerosa.ontrack.extension.scm.changelog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BuildDiff;
import net.nemerosa.ontrack.model.structure.BuildView;

/**
 * @param <S> Type of SCM data associated with the branch
 * @param <T> Type of SCM data associated with a build
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SCMChangeLog<S, T> extends BuildDiff {

    private final S scmBranch;
    private final SCMBuildView<T> scmBuildFrom;
    private final SCMBuildView<T> scmBuildTo;

    protected SCMChangeLog(Branch branch, S scmBranch, SCMBuildView<T> scmBuildFrom, SCMBuildView<T> scmBuildTo) {
        super(branch);
        this.scmBranch = scmBranch;
        this.scmBuildFrom = scmBuildFrom;
        this.scmBuildTo = scmBuildTo;
    }

    @Override
    public BuildView getFrom() {
        return scmBuildFrom.getBuildView();
    }

    @Override
    public BuildView getTo() {
        return scmBuildTo.getBuildView();
    }

    public static <S, T> SCMChangeLog<S, T> of(Branch branch, S scmBranch, SCMBuildView<T> from, SCMBuildView<T> to) {
        // TODO Checks the order of the builds
        return new SCMChangeLog<>(
                branch,
                scmBranch,
                from,
                to
        );
    }

}
