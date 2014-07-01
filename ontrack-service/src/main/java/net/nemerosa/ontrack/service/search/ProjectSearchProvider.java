package net.nemerosa.ontrack.service.search;

import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

@Component
public class ProjectSearchProvider implements SearchProvider {

    private final StructureService structureService;

    @Autowired
    public ProjectSearchProvider(StructureService structureService) {
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
                            null,
                            100
                    )
            );
        } else {
            return Collections.emptyList();
        }
    }
}
