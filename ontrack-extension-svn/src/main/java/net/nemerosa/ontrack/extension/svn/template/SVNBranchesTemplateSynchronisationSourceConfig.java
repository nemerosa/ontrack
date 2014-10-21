package net.nemerosa.ontrack.extension.svn.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.model.SCMBranchesTemplateSynchronisationSourceConfig;

import java.beans.ConstructorProperties;

@EqualsAndHashCode(callSuper = false)
@Data
public class SVNBranchesTemplateSynchronisationSourceConfig extends SCMBranchesTemplateSynchronisationSourceConfig {

    @ConstructorProperties({"includes", "excludes"})
    public SVNBranchesTemplateSynchronisationSourceConfig(String includes, String excludes) {
        super(includes, excludes);
    }
}
