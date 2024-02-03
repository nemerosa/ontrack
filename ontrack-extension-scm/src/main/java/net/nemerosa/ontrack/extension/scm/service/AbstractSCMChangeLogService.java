package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLog;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.extension.scm.property.SCMChangeLogIssueValidator;
import net.nemerosa.ontrack.model.structure.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <S> Type of SCM data associated with the branch
 * @param <T> Type of SCM data associated with a build
 * @param <I> Type of issue associated with this change log
 * @deprecated Use the new scm.SCMChangeLogService
 */
@Deprecated
public abstract class AbstractSCMChangeLogService<S, T, I extends SCMChangeLogIssue> {

    protected final StructureService structureService;
    protected final PropertyService propertyService;

    protected AbstractSCMChangeLogService(StructureService structureService, PropertyService propertyService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
    }

    protected BuildView getBuildView(ID id) {
        return structureService.getBuildView(structureService.getBuild(id), true);
    }


    protected void validateIssues(List<I> issuesList, SCMChangeLog<T> changeLog) {
        // Same branch only
        if (changeLog.isSameBranch()) {
            // Gets the branch
            Branch branch = changeLog.getFrom().getBuild().getBranch();
            // Gets the list of validators for this branch
            List<Property<?>> properties = propertyService.getProperties(branch).stream()
                    .filter(property -> !property.isEmpty() && property.getType() instanceof SCMChangeLogIssueValidator)
                    .collect(Collectors.toList());
            // Validates each issues
            issuesList.forEach(issue -> validateIssue(issue, properties, changeLog));
        }
    }

    protected void validateIssue(I issue, List<Property<?>> properties, SCMChangeLog<T> changeLog) {
        for (Property<?> property : properties) {
            if (!property.isEmpty()) {
                validateIssue(issue, property, changeLog);
            }
        }
    }

    private <C> void validateIssue(I issue, Property<C> property, SCMChangeLog<T> changeLog) {
        SCMChangeLogIssueValidator<C, S, T, I> validator = (SCMChangeLogIssueValidator<C, S, T, I>) property.getType();
        validator.validate(changeLog, issue, property.getValue());
    }

}
