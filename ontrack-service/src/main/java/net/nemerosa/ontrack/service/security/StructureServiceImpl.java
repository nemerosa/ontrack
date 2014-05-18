package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.nemerosa.ontrack.model.structure.Entity.isEntityDefined;
import static net.nemerosa.ontrack.model.structure.Entity.isEntityNew;

@Service
@Transactional
public class StructureServiceImpl implements StructureService {

    private final SecurityService securityService;
    private final StructureRepository structureRepository;

    @Autowired
    public StructureServiceImpl(SecurityService securityService, StructureRepository structureRepository) {
        this.securityService = securityService;
        this.structureRepository = structureRepository;
    }

    @Override
    public Project newProject(Project project) {
        securityService.checkGlobalFunction(ProjectCreation.class);
        return structureRepository.newProject(project);
    }

    @Override
    public List<Project> getProjectList() {
        return structureRepository.getProjectList();
    }

    @Override
    public Project getProject(ID projectId) {
        // TODO Security
        return structureRepository.getProject(projectId);
    }

    @Override
    public void saveProject(Project project) {
        // TODO Security
        structureRepository.saveProject(project);
    }

    @Override
    public Branch getBranch(ID branchId) {
        // TODO Security
        return structureRepository.getBranch(branchId);
    }

    @Override
    public List<Branch> getBranchesForProject(ID projectId) {
        // TODO Security
        return structureRepository.getBranchesForProject(projectId);
    }

    @Override
    public Branch newBranch(Branch branch) {
        // Validation
        isEntityNew(branch, "Branch must be new");
        isEntityDefined(branch.getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(branch.getProject().id(), ProjectEdit.class);
        // OK
        return structureRepository.newBranch(branch);
    }

    @Override
    public Build newBuild(Build build) {
        // TODO Security
        return structureRepository.newBuild(build);
    }

    @Override
    public Build saveBuild(Build build) {
        // TODO Security
        return structureRepository.saveBuild(build);
    }
}
