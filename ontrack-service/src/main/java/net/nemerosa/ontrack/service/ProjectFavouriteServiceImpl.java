package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService;
import net.nemerosa.ontrack.repository.ProjectFavouriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectFavouriteServiceImpl implements ProjectFavouriteService {

    private final ProjectFavouriteRepository repository;
    private final SecurityService securityService;

    @Autowired
    public ProjectFavouriteServiceImpl(ProjectFavouriteRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    @Override
    public boolean isProjectFavourite(Project project) {
        return securityService.isProjectFunctionGranted(project, ProjectView.class) &&
                securityService.getAccount().filter(account -> account.getId().isSet())
                        .map(account -> repository.isProjectFavourite(
                                account.id(),
                                project.id()
                        )).orElse(false);
    }

    @Override
    public void setProjectFavourite(Project project, boolean favourite) {
        if (securityService.isProjectFunctionGranted(project, ProjectView.class)) {
            securityService.getAccount().ifPresent(account ->
                    repository.setProjectFavourite(account.id(), project.id(), favourite)
            );
        }
    }
}
