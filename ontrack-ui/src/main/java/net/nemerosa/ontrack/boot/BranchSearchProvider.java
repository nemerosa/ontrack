package net.nemerosa.ontrack.boot;

import net.nemerosa.ontrack.boot.ui.BranchController;
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
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class BranchSearchProvider extends AbstractSearchProvider {

    private final StructureService structureService;

    @Autowired
    public BranchSearchProvider(URIBuilder uriBuilder, StructureService structureService) {
        super(uriBuilder);
        this.structureService = structureService;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches(NameDescription.NAME, token);
    }

    @Override
    public Collection<SearchResult> search(String token) {
        return structureService
                // Gets the list of authorized projects
                .getProjectList().stream()
                        // Gets their name
                .map(Project::getName)
                        // Looks for a branch
                .map(project -> structureService.findBranchByName(project, token))
                        // Keeps only the positive results
                .filter(Optional::isPresent).map(Optional::get)
                        // Creates the search result
                .map(branch -> new SearchResult(
                                format("%s / %s", branch.getProject().getName(), branch.getName()),
                                format("%s branch on project %s", branch.getName(), branch.getProject().getName()),
                                uri(MvcUriComponentsBuilder.on(BranchController.class).getBranch(branch.getId())),
                                format("/branch/%d", branch.id()),
                                100
                        )
                )
                        // Conversion to list
                .collect(Collectors.toList())
                ;
    }
}
