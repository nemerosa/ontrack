package net.nemerosa.ontrack.extension.scm.changelog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BuildDiff;
import net.nemerosa.ontrack.model.structure.BuildView;

@EqualsAndHashCode(callSuper = false)
@Data
public class SCMChangeLog extends BuildDiff {

    protected SCMChangeLog(Branch branch, BuildView fromBuild, BuildView toBuild) {
        super(branch, fromBuild, toBuild);
    }

    public static SCMChangeLog of(Branch branch, BuildView from, BuildView to) {
        // TODO Checks the order of the builds
        return new SCMChangeLog(
                branch,
                from,
                to
        );
    }

}
