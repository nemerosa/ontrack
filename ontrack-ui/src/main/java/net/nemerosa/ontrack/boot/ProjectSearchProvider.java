package net.nemerosa.ontrack.boot;

import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

@Component
public class ProjectSearchProvider extends AbstractSearchProvider {

    private final StructureService structureService;

    @Autowired
    public ProjectSearchProvider(URIBuilder uriBuilder, StructureService structureService) {
        super(uriBuilder);
        this.structureService = structureService;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches(NameDescription.NAME, token);
    }

    @Override
    public Collection<SearchResult> search(String token) {
        Project project = structureService.findProjectByName(token);
        if (project != null) {
            return Collections.singletonList(
                    new SearchResult(
                            project.getName(),
                            String.format("%s project", project.getName()),
                            uri(MvcUriComponentsBuilder.on(ProjectController.class).getProject(project.getId())),
                            String.format("/project/%d", project.id()),
                            100
                    )
            );
        } else {
            return Collections.emptyList();
        }
    }
}
