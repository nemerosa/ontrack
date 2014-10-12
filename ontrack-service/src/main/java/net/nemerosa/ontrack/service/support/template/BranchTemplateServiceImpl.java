package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.exceptions.BranchTemplateHasBuildException;
import net.nemerosa.ontrack.model.exceptions.BranchTemplateInstanceException;
import net.nemerosa.ontrack.model.security.BranchTemplateMgt;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BranchTemplateServiceImpl implements BranchTemplateService {

    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public BranchTemplateServiceImpl(StructureService structureService, SecurityService securityService) {
        this.structureService = structureService;
        this.securityService = securityService;
    }

    @Override
    public Optional<TemplateDefinition> getTemplateDefinition(ID branchId) {
        // FIXME Method net.nemerosa.ontrack.service.support.template.BranchTemplateServiceImpl.getTemplateDefinition
        return Optional.empty();
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
        // FIXME Method net.nemerosa.ontrack.service.support.template.BranchTemplateServiceImpl.setTemplateDefinition
        // Reloads the branch
        return structureService.getBranch(branchId);
    }
}
