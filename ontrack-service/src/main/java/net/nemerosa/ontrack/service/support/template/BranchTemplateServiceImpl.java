package net.nemerosa.ontrack.service.support.template;

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

    @Autowired
    public BranchTemplateServiceImpl(StructureService structureService, SecurityService securityService, BranchTemplateRepository branchTemplateRepository) {
        this.structureService = structureService;
        this.securityService = securityService;
        this.branchTemplateRepository = branchTemplateRepository;
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
}
