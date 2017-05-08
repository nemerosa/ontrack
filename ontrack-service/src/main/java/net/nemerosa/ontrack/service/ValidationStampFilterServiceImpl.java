package net.nemerosa.ontrack.service;

import com.google.common.collect.ImmutableList;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.structure.ValidationStampFilter;
import net.nemerosa.ontrack.repository.ValidationStampFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Transactional
public class ValidationStampFilterServiceImpl implements ValidationStampFilterService {

    private final ValidationStampFilterRepository repository;
    private final SecurityService securityService;

    @Autowired
    public ValidationStampFilterServiceImpl(ValidationStampFilterRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    @Override
    public List<ValidationStampFilter> getGlobalValidationStampFilters() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return repository.getGlobalValidationStampFilters();
    }

    @Override
    public List<ValidationStampFilter> getProjectValidationStampFilters(Project project, boolean includeAll) {
        securityService.checkProjectFunction(project, ProjectView.class);
        // Index by names
        Map<String, ValidationStampFilter> filters = new TreeMap<>();
        // Gets the global filters
        if (includeAll) {
            repository.getGlobalValidationStampFilters().forEach(f ->
                    filters.put(f.getName(), f)
            );
        }
        // Gets for project
        repository.getProjectValidationStampFilters(project).forEach(f ->
                filters.put(f.getName(), f)
        );
        // OK
        return ImmutableList.copyOf(filters.values());
    }

    @Override
    public List<ValidationStampFilter> getBranchValidationStampFilters(Branch branch, boolean includeAll) {
        securityService.checkProjectFunction(branch, ProjectView.class);
        // Index by names
        Map<String, ValidationStampFilter> filters = new TreeMap<>();
        // Gets the project filters
        if (includeAll) {
            repository.getGlobalValidationStampFilters().forEach(f ->
                    filters.put(f.getName(), f)
            );
            repository.getProjectValidationStampFilters(branch.getProject()).forEach(f ->
                    filters.put(f.getName(), f)
            );
        }
        // Gets for branch
        repository.getBranchValidationStampFilters(branch).forEach(f ->
                filters.put(f.getName(), f)
        );
        // OK
        return ImmutableList.copyOf(filters.values());
    }

    @Override
    public Optional<ValidationStampFilter> getValidationStampFilterByName(Branch branch, String name) {
        securityService.checkProjectFunction(branch, ProjectView.class);
        return repository.getValidationStampFilterByName(branch, name);
    }

    @Override
    public ValidationStampFilter newValidationStampFilter(ValidationStampFilter filter) {
        checkUpdateAuthorisations(filter);
        return repository.newValidationStampFilter(filter);
    }

    @Override
    public void saveValidationStampFilter(ValidationStampFilter filter) {
        checkUpdateAuthorisations(filter);
        repository.saveValidationStampFilter(filter);
    }

    @Override
    public Ack deleteValidationStampFilter(ValidationStampFilter filter) {
        checkUpdateAuthorisations(filter);
        return repository.deleteValidationStampFilter(filter.getId());
    }

    private void checkUpdateAuthorisations(ValidationStampFilter filter) {
        if (filter.getProject() != null) {
            securityService.checkProjectFunction(filter.getProject(), ProjectConfig.class);
        } else if (filter.getBranch() != null) {
            securityService.checkProjectFunction(filter.getBranch(), ProjectConfig.class);
        } else {
            securityService.checkGlobalFunction(GlobalSettings.class);
        }
    }

    @Override
    public ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter, Project project) {
        securityService.checkProjectFunction(project, ProjectConfig.class);
        return repository.shareValidationStampFilter(filter, project);
    }

    @Override
    public ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return repository.shareValidationStampFilter(filter);
    }

    @Override
    public ValidationStampFilter getValidationStampFilter(ID id) {
        return repository.getValidationStampFilter(id);
    }
}
