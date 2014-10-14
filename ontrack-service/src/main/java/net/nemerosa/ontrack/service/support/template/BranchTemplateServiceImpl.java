package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.exceptions.BranchClassicCannotBeTemplateInstanceException;
import net.nemerosa.ontrack.model.exceptions.BranchNotTemplateDefinitionException;
import net.nemerosa.ontrack.model.exceptions.BranchTemplateHasBuildException;
import net.nemerosa.ontrack.model.exceptions.BranchTemplateInstanceException;
import net.nemerosa.ontrack.model.security.BranchTemplateMgt;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.BranchTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Branch createTemplateInstance(ID branchId, String branchName) {
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
        Optional<Branch> existingBranch = structureService.findBranchByName(branch.getProject().getName(), branchName);

        // Not existing
        if (!existingBranch.isPresent()) {
            // Creates the branch
            Branch instance = structureService.newBranch(
                    Branch.of(
                            branch.getProject(),
                            NameDescription.nd(
                                    branchName,
                                    ""
                            )
                    )
            );
            // Updates the branch
            return updateTemplateInstance(instance, branch, templateDefinitionOptional.get());
        }
        // Existing, normal branch
        else if (existingBranch.get().getType() == BranchType.CLASSIC) {
            throw new BranchClassicCannotBeTemplateInstanceException(branchName);
        } else {
            throw new RuntimeException("NYI Gets the linked definition");
            // TODO Gets the linked definition
            // TODO If same definition, updates the branch
            // TODO If another definition, error
            // TODO If normal branch, error
        }
    }

    protected Branch updateTemplateInstance(Branch instance, Branch template, TemplateDefinition templateDefinition) {
        // Description of the branch
        String description = templateDefinition.apply(template.getDescription(), instance.getName(), expressionEngine);
        instance = instance.withDescription(description);
        structureService.saveBranch(instance);
        // FIXME Copy
        // OK - reloads to gets the correct type
        return structureService.getBranch(instance.getId());
    }
}
