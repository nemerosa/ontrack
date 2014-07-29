package net.nemerosa.ontrack.extension.scm.property;

import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractSCMChangeLogIssueValidator<T> extends AbstractPropertyType<T> implements SCMChangeLogIssueValidator<T> {

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BRANCH);
    }

}
