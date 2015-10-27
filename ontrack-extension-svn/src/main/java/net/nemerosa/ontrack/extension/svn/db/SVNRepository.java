package net.nemerosa.ontrack.extension.svn.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import org.tmatesoft.svn.core.SVNURL;

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
        return ".*/branches/[^/]+";
    }

    public String getTagPattern() {
        return ".*/tags/[^/]+";
    }

    public String getUrl(String path) {
        return configuration.getUrl(path);
    }

    public String getRevisionBrowsingURL(long revision) {
        return configuration.getRevisionBrowsingURL(revision);
    }

    public String getPathBrowsingURL(String path) {
        return configuration.getPathBrowsingURL(path);
    }

    public String getFileChangeBrowsingURL(String path, long revision) {
        return configuration.getFileChangeBrowsingURL(path, revision);
    }

    public SVNURL getRootUrl() {
        return SVNUtils.toURL(configuration.getUrl());
    }
}
