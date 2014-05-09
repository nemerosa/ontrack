package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.structure.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UIResourceAssembler implements ResourceAssembler {

    private final ApplicationContext applicationContext;

    @Autowired
    public UIResourceAssembler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private UIStructure structure() {
        return applicationContext.getBean(UIStructure.class);
    }

    @Override
    public Resource<Project> toProjectResource(Project project) {
        return Resource.of(project)
                // TODO Resource: get project
                // .self(link(fromMethodCall(on(UIStructureController.class).project(project.getId()))))
                // TODO Resource: list of branches
                ;
    }
}
