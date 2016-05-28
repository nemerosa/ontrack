package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class BuildLinkProperty {

    private final List<BuildLinkPropertyItem> links;

    /**
     * Constructor, which filters out the duplicates.
     */
    @ConstructorProperties({"links"})
    public BuildLinkProperty(List<BuildLinkPropertyItem> links) {
        this.links = links.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Does one of the items match the project-&gt;build? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    public boolean match(String projectPattern, String buildPattern) {
        return links.stream().anyMatch(item -> item.match(projectPattern, buildPattern));
    }

    /**
     * Gets the build value for a given project
     */
    public Optional<String> getBuild(String project) {
        return links.stream()
                .filter(item -> StringUtils.equals(project, item.getProject()))
                .map(BuildLinkPropertyItem::getBuild)
                .findFirst();
    }

}
