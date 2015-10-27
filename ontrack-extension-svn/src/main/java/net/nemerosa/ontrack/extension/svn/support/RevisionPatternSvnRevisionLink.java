package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.model.SVNLocation;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;

/**
 * Build / revision relationship based on the build name contained the revision.
 */
@Component
public class RevisionPatternSvnRevisionLink implements BuildSvnRevisionLink<RevisionPattern> {

    private final StructureService structureService;

    @Autowired
    public RevisionPatternSvnRevisionLink(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public String getId() {
        return "revisionPattern";
    }

    @Override
    public String getName() {
        return "Build name contains a revision";
    }

    @Override
    public RevisionPattern clone(RevisionPattern data, Function<String, String> replacementFunction) {
        return data;
    }

    @Override
    public RevisionPattern parseData(JsonNode node) {
        try {
            return ObjectMapperFactory.create().treeToValue(node, RevisionPattern.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(e);
        }
    }

    @Override
    public JsonNode toJson(RevisionPattern data) {
        return ObjectMapperFactory.create().valueToTree(data);
    }

    @Override
    public Form getForm() {
        return Form.create()
                .with(
                        Text.of("pattern")
                                .label("Revision pattern")
                                .help("@file:extension/svn/buildRevisionLink/revisionPattern.help.tpl.html")
                )
                ;
    }

    @Override
    public boolean isValidBuildName(RevisionPattern data, String name) {
        return data.isValidBuildName(name);
    }

    @Override
    public OptionalLong getRevision(RevisionPattern data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        return data.extractRevision(build.getName());
    }

    @Override
    public String getBuildPath(RevisionPattern data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        return branchConfigurationProperty.getBranchPath() + "@" + getRevision(data, build, branchConfigurationProperty).getAsLong();
    }

    @Override
    public Optional<Build> getEarliestBuild(RevisionPattern data, Branch branch, SVNLocation location, SVNLocation firstCopy, SVNBranchConfigurationProperty branchConfigurationProperty) {
        // FIXME Earliest build using revision pattern
        // Checks the path
        if (StringUtils.equals(branchConfigurationProperty.getBranchPath(), location.getPath())) {
            String buildName = String.valueOf(location.getRevision());
            return structureService.findBuildAfterUsingNumericForm(branch.getId(), buildName);
        } else {
            return Optional.empty();
        }
    }
}
