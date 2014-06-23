package net.nemerosa.ontrack.extension.svn.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.svn.SVNConfiguration;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SVNRepository {

    private final int id;
    private final SVNConfiguration configuration;
    private final ConfiguredIssueService configuredIssueService;

    public static SVNRepository of(int id, SVNConfiguration configuration, ConfiguredIssueService configuredIssueService) {
        return new SVNRepository(id, configuration, configuredIssueService);
    }

    public String getBranchPattern() {
        String branchPattern = configuration.getBranchPattern();
        if (StringUtils.isNotBlank(branchPattern)) {
            return branchPattern;
        } else {
            return ".*/branches/.+";
        }
    }

    public String getTagPattern() {
        String tagPattern = configuration.getTagPattern();
        if (StringUtils.isNotBlank(tagPattern)) {
            return tagPattern;
        } else {
            return ".*/tags/.+";
        }
    }

    public String getUrl(String path) {
        return configuration.getUrl(path);
    }
}
