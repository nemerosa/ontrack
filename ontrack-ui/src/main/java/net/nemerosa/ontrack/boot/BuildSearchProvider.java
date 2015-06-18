package net.nemerosa.ontrack.boot;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.SearchResult;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class BuildSearchProvider extends AbstractSearchProvider {

    private final StructureService structureService;

    @Autowired
    public BuildSearchProvider(URIBuilder uriBuilder, StructureService structureService) {
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
                        // Gets the list of branches
                .flatMap(project -> structureService.getBranchesForProject(project.getId()).stream())
                        // Looks for the builds with the name to search
                .map(branch -> structureService.findBuildByName(branch.getProject().getName(), branch.getName(), token))
                        // Keeps only the positive results
                .filter(Optional::isPresent).map(Optional::get)
                        // Creates the search result
                .map(build -> new SearchResult(
                                build.getEntityDisplayName(),
                                "",
                                uriBuilder.getEntityURI(build),
                                uriBuilder.getEntityPage(build),
                                100
                        )
                )
                        // Conversion to list
                .collect(Collectors.toList())
                ;
    }
}
