package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.exceptions.*;
import net.nemerosa.ontrack.model.security.BranchTemplateMgt;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.BranchTemplateRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class BranchTemplateServiceImpl implements BranchTemplateService {

    private final StructureService structureService;
    private final SecurityService securityService;
    private final BranchTemplateRepository branchTemplateRepository;
    private final ExpressionEngine expressionEngine;
    private final CopyService copyService;

    @Autowired
    public BranchTemplateServiceImpl(StructureService structureService, SecurityService securityService, BranchTemplateRepository branchTemplateRepository, ExpressionEngine expressionEngine, CopyService copyService) {
        this.structureService = structureService;
        this.securityService = securityService;
        this.branchTemplateRepository = branchTemplateRepository;
        this.expressionEngine = expressionEngine;
        this.copyService = copyService;
    }

    @Override
    public Optional<TemplateDefinition> getTemplateDefinition(ID branchId) {
        return branchTemplateRepository.getTemplateDefinition(branchId);
    }

    @Override
    public Branch setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition) {
        // Loads the branch
        Branch branch = structureService.getBranch(branchId);
        // Checks the rights
        securityService.checkProjectFunction(branch, BranchTemplateMgt.class);
        // Checks the branch is NOT an instance
        if (branch.getType() == BranchType.TEMPLATE_INSTANCE) {
            throw new BranchTemplateInstanceException(branch.getName());
        }
        // Checks the builds
        int buildCount = structureService.getBuildCount(branch);
        if (buildCount > 0) {
            throw new BranchTemplateHasBuildException(branch.getName());
        }
        // TODO In case of updates, checks for impact on the instances
        // Saves the definition
        branchTemplateRepository.setTemplateDefinition(branchId, templateDefinition);
        // Reloads the branch
        return structureService.getBranch(branchId);
    }

    @Override
    public Branch createTemplateInstance(ID branchId, BranchTemplateInstanceSingleRequest request) {
        // Loads the branch template definition
        Branch branch = structureService.getBranch(branchId);
        // Gets the template definition
        Optional<TemplateDefinition> templateDefinitionOptional = branchTemplateRepository.getTemplateDefinition(branchId);
        if (!templateDefinitionOptional.isPresent()) {
            throw new BranchNotTemplateDefinitionException(branchId);
        }
        // Checks the rights
        securityService.checkProjectFunction(branch, BranchTemplateMgt.class);

        // Gets the existing branch
        Optional<Branch> existingBranch = structureService.findBranchByName(branch.getProject().getName(), request.getName());

        // Not existing
        if (!existingBranch.isPresent()) {
            // Creates the branch
            Branch instance = structureService.newBranch(
                    Branch.of(
                            branch.getProject(),
                            NameDescription.nd(
                                    request.getName(),
                                    ""
                            )
                    )
            );
            // Updates the branch
            return updateTemplateInstance(instance, branch, request, templateDefinitionOptional.get());
        }
        // Existing, normal branch
        else if (existingBranch.get().getType() == BranchType.CLASSIC) {
            throw new BranchClassicCannotBeTemplateInstanceException(request.getName());
        }
        // Existing, template branch
        else if (existingBranch.get().getType() == BranchType.TEMPLATE_DEFINITION) {
            throw new BranchTemplateDefinitionCannotBeTemplateInstanceException(request.getName());
        } else {
            // Gets the template instance
            Optional<TemplateInstance> templateInstanceOptional = branchTemplateRepository.getTemplateInstance(existingBranch.get().getId());
            // This must be a template instance then...
            Validate.isTrue(templateInstanceOptional.isPresent(), "A template instance branch must have a template instance object attached to it");
            // Gets the linked definition
            ID linkedTemplateId = templateInstanceOptional.get().getTemplateDefinitionId();
            // If another definition, error
            if (!Objects.equals(linkedTemplateId, branchId)) {
                throw new BranchTemplateInstanceCannotUpdateBasedOnOtherDefinitionException(request.getName());
            }
            // If same definition, updates the branch
            else {
                return updateTemplateInstance(existingBranch.get(), branch, request, templateDefinitionOptional.get());
            }
        }
    }

    protected Branch updateTemplateInstance(Branch instance, Branch template, BranchTemplateInstanceSingleRequest request, TemplateDefinition templateDefinition) {
        // Manual mode
        if (request.isManual()) {
            // Missing parameters
            List<String> templateParameterNames = templateDefinition.getParameters().stream()
                    .map(TemplateParameter::getName)
                    .collect(Collectors.toList());
            Set<String> missingParameters = new HashSet<>(templateParameterNames);
            missingParameters.removeAll(request.getParameters().keySet());
            if (missingParameters.size() > 0) {
                throw new BranchTemplateInstanceMissingParametersException(template.getName(), missingParameters);
            }
            // Unknown parameters
            Set<String> unknownParameters = new HashSet<>(request.getParameters().keySet());
            unknownParameters.removeAll(templateParameterNames);
            if (unknownParameters.size() > 0) {
                throw new BranchTemplateInstanceUnknownParametersException(template.getName(), unknownParameters);
            }
            // Adds `branchName` as a parameter
            Map<String, String> engineParams = new HashMap<>(request.getParameters());
            engineParams.put("branchName", instance.getName());
            // Replacement function
            Function<String, String> replacementFn = value -> expressionEngine.render(value, engineParams);
            // Template instance execution context
            TemplateInstanceExecution templateInstanceExecution = new TemplateInstanceExecution(
                    replacementFn,
                    request.getParameters()
            );
            // OK
            return updateTemplateInstance(instance, template, templateInstanceExecution);
        }
        // Automatic mode
        else {
            return updateTemplateInstance(instance, template, templateDefinition);
        }
    }

    protected Branch updateTemplateInstance(Branch instance, Branch template, TemplateDefinition templateDefinition) {
        return updateTemplateInstance(
                instance,
                template,
                templateDefinition.templateInstanceExecution(instance.getName(), expressionEngine)
        );

    }

    protected Branch updateTemplateInstance(Branch instance, Branch template, TemplateInstanceExecution templateInstanceExecution) {
        // Description of the branch
        String description = templateInstanceExecution.replace(template.getDescription());
        instance = instance.withDescription(description);
        structureService.saveBranch(instance);
        // Copy replacement function
        copyService.copy(
                instance, // Target
                template, // Source
                templateInstanceExecution.getReplacementFn(),
                SyncPolicy.SYNC
        );
        // Template instance
        TemplateInstance templateInstance = new TemplateInstance(
                template.getId(),
                templateInstanceExecution.asTemplateParameterValues()
        );
        // Branch as a branch template instance
        branchTemplateRepository.setTemplateInstance(instance.getId(), templateInstance);
        // OK - reloads to gets the correct type
        return structureService.getBranch(instance.getId());
    }
}
