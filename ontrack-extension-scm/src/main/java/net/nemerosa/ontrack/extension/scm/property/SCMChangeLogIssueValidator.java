package net.nemerosa.ontrack.extension.scm.property;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLog;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.model.structure.PropertyType;

/**
 * @param <T> The configuration for this validator property
 * @param <S> Type of SCM data associated with the branch
 * @param <B> Type of SCM data associated with a build
 * @param <I> Type of issue associated with this change log
 *
 * @deprecated Will be removed in V5.
 */
@Deprecated
public interface SCMChangeLogIssueValidator<T, S, B, I extends SCMChangeLogIssue> extends PropertyType<T> {

    void validate(SCMChangeLog<B> changeLog, I issue, T validatorConfig);

}
