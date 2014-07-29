package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.extension.scm.property.SCMChangeLogIssueValidator;
import net.nemerosa.ontrack.model.structure.*;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractSCMChangeLogService {

    protected final StructureService structureService;
    protected final PropertyService propertyService;

    protected AbstractSCMChangeLogService(StructureService structureService, PropertyService propertyService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
    }

    protected BuildView getBuildView(ID id) {
        return structureService.getBuildView(structureService.getBuild(id));
    }


    protected void validateIssues(List<? extends SCMChangeLogIssue> issuesList, Branch branch) {
        // Gets the list of validators for this branch
        List<Property<?>> properties = propertyService.getProperties(branch).stream()
                .filter(property -> !property.isEmpty() && property.getType() instanceof SCMChangeLogIssueValidator)
                .collect(Collectors.toList());
        // Validates each issues
        issuesList.forEach(issue -> validateIssue(issue, properties, branch));
    }

    @SuppressWarnings("unchecked")
    protected void validateIssue(SCMChangeLogIssue issue, List<Property<?>> properties, Branch branch) {
        properties.forEach(property -> validateIssue(branch, issue, (SCMChangeLogIssueValidator) property.getType(), property.getValue()));
    }

    private <T> void validateIssue(Branch branch, SCMChangeLogIssue issue, SCMChangeLogIssueValidator<T> validator, T validatorConfig) {
        validator.validate(branch, issue, validatorConfig);
    }

}
