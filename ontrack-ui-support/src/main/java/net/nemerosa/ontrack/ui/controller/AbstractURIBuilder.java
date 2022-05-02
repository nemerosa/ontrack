package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.apache.commons.text.CaseUtils;

import java.net.URI;

import static java.lang.String.format;

public abstract class AbstractURIBuilder implements URIBuilder {

    public String pagePath(String path, Object... arguments) {
        return format(
                "/#/%s",
                format(path, arguments)
        );
    }

    public String getEntityPageName(ProjectEntityType projectEntityType) {
        return CaseUtils.toCamelCase(
                projectEntityType.name().toLowerCase(),
                false,
                '_'
        );
    }

    @Override
    public URI getEntityPage(ProjectEntity entity) {
        return page(
                "%s/%d",
                getEntityPageName(entity.getProjectEntityType()),
                entity.id()
        );
    }
}
