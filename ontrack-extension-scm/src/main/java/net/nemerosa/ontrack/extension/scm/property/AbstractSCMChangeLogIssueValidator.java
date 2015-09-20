package net.nemerosa.ontrack.extension.scm.property;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;

/**
 * @param <T> The configuration for this validator property
 * @param <S> Type of SCM data associated with the branch
 * @param <B> Type of SCM data associated with a build
 * @param <I> Type of issue associated with this change log
 */
public abstract class AbstractSCMChangeLogIssueValidator<T, S, B, I extends SCMChangeLogIssue> extends AbstractPropertyType<T> implements SCMChangeLogIssueValidator<T, S, B, I> {

    public AbstractSCMChangeLogIssueValidator(ExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BRANCH);
    }

}
