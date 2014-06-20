package net.nemerosa.ontrack.extension.svn.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.scm.changelog.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.svn.MissingSVNProjectConfigurationException;
import net.nemerosa.ontrack.extension.svn.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SVNChangeLogServiceImpl extends AbstractSCMChangeLogService implements SVNChangeLogService {

    private final PropertyService propertyService;
    private final SVNRepositoryDao repositoryDao;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public SVNChangeLogServiceImpl(
            StructureService structureService,
            PropertyService propertyService, SVNRepositoryDao repositoryDao, IssueServiceRegistry issueServiceRegistry) {
        super(structureService);
        this.propertyService = propertyService;
        this.repositoryDao = repositoryDao;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public SVNChangeLog changeLog(BuildDiffRequest request) {
        Branch branch = structureService.getBranch(request.getBranch());
        SVNRepository svnRepository = getSVNRepository(branch);
        return new SVNChangeLog(
                branch,
                svnRepository,
                getSCMBuildView(svnRepository, request.getFrom()),
                getSCMBuildView(svnRepository, request.getTo())
        );
    }

    protected SCMBuildView<SVNHistory> getSCMBuildView(SVNRepository svnRepository, ID buildId) {
        // Gets the build view
        BuildView buildView = getBuildView(buildId);
        // Gets the history for the build
        SVNHistory history = getBuildSVNHistory(svnRepository, buildView.getBuild());
        // OK
        return new SCMBuildView<>(buildView, history);
    }

    private SVNHistory getBuildSVNHistory(SVNRepository svnRepository, Build build) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.changelog.SVNChangeLogServiceImpl.getBuildSVNHistory
        return null;
    }

    protected SVNRepository getSVNRepository(Branch branch) {
        // Gets the SVN project configuration property
        Property<SVNProjectConfigurationProperty> projectConfiguration = propertyService.getProperty(
                branch.getProject(),
                SVNProjectConfigurationPropertyType.class
        );
        if (projectConfiguration.isEmpty()) {
            throw new MissingSVNProjectConfigurationException(branch.getProject().getName());
        } else {
            SVNConfiguration configuration = projectConfiguration.getValue().getConfiguration();
            return SVNRepository.of(
                    repositoryDao.getOrCreateByName(configuration.getName()),
                    configuration,
                    issueServiceRegistry.getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier())
            );
        }
    }

}
