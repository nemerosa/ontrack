package net.nemerosa.ontrack.extension.git;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.model.SCMBranchesTemplateSynchronisationSourceConfig;

import java.beans.ConstructorProperties;

@EqualsAndHashCode(callSuper = false)
@Data
public class GitBranchesTemplateSynchronisationSourceConfig extends SCMBranchesTemplateSynchronisationSourceConfig {

    @ConstructorProperties({"includes", "excludes"})
    public GitBranchesTemplateSynchronisationSourceConfig(String includes, String excludes) {
        super(includes, excludes);
    }
}
