package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.*;
import net.nemerosa.ontrack.extension.svn.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SVNServiceImpl implements SVNService {

    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNConfigurationService configurationService;
    private final SVNRevisionDao revisionDao;
    private final SVNIssueRevisionDao issueRevisionDao;
    private final SVNRepositoryDao repositoryDao;
    private final SVNClient svnClient;

    @Autowired
    public SVNServiceImpl(
            IssueServiceRegistry issueServiceRegistry,
            SVNConfigurationService configurationService,
            SVNRevisionDao revisionDao,
            SVNIssueRevisionDao issueRevisionDao,
            SVNRepositoryDao repositoryDao,
            SVNClient svnClient) {
        this.issueServiceRegistry = issueServiceRegistry;
        this.configurationService = configurationService;
        this.revisionDao = revisionDao;
        this.issueRevisionDao = issueRevisionDao;
        this.repositoryDao = repositoryDao;
        this.svnClient = svnClient;
    }

    @Override
    public SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision) {
        TRevision t = revisionDao.get(repository.getId(), revision);
        return new SVNRevisionInfo(
                t.getRevision(),
                t.getAuthor(),
                t.getCreation(),
                t.getBranch(),
                t.getMessage(),
                repository.getRevisionBrowsingURL(t.getRevision())
        );
    }

    @Override
    public SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision) {
        // Gets the diff for the revision
        List<SVNRevisionPath> revisionPaths = svnClient.getRevisionPaths(repository, revision);
        // OK
        return new SVNRevisionPaths(
                getRevisionInfo(repository, revision),
                revisionPaths);
    }

    @Override
    public SVNRepository getRepository(String name) {
        SVNConfiguration configuration = configurationService.getConfiguration(name);
        return SVNRepository.of(
                repositoryDao.getOrCreateByName(configuration.getName()),
                // The configuration contained in the property's configuration is obfuscated
                // and the original one must be loaded
                configuration,
                issueServiceRegistry.getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier())
        );
    }

    @Override
    public Optional<SVNRepositoryIssue> searchIssues(SVNRepository repository, String token) {
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        if (configuredIssueService != null) {
            return issueRevisionDao.findIssueByKey(repository.getId(), token)
                    .map(key -> new SVNRepositoryIssue(
                                    repository,
                                    configuredIssueService.getIssue(key)
                            )
                    );
        } else {
            return Optional.empty();
        }
    }
}
