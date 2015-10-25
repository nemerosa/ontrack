package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.*;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.SVNConfigProperties;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Service
@Transactional
public class SVNServiceImpl implements SVNService {

    private final Logger logger = LoggerFactory.getLogger(SVNService.class);

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNConfigurationService configurationService;
    private final SVNRevisionDao revisionDao;
    private final SVNIssueRevisionDao issueRevisionDao;
    private final SVNEventDao eventDao;
    private final SVNRepositoryDao repositoryDao;
    private final SVNClient svnClient;
    private final TransactionService transactionService;
    private final SecurityService securityService;
    private final SVNConfigProperties svnConfigProperties;

    @Autowired
    public SVNServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            IssueServiceRegistry issueServiceRegistry,
            SVNConfigurationService configurationService,
            SVNRevisionDao revisionDao,
            SVNIssueRevisionDao issueRevisionDao,
            SVNEventDao eventDao,
            SVNRepositoryDao repositoryDao,
            SVNClient svnClient,
            TransactionService transactionService,
            SVNConfigProperties svnConfigProperties,
            SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.configurationService = configurationService;
        this.revisionDao = revisionDao;
        this.issueRevisionDao = issueRevisionDao;
        this.eventDao = eventDao;
        this.repositoryDao = repositoryDao;
        this.svnClient = svnClient;
        this.transactionService = transactionService;
        this.securityService = securityService;
        this.svnConfigProperties = svnConfigProperties;
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
    public List<Long> getRevisionsForIssueKey(SVNRepository repository, String key) {
        return issueRevisionDao.findRevisionsByIssue(repository.getId(), key);
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
            return configuredIssueService
                    .getIssueId(token)
                    .flatMap(searchKey -> issueRevisionDao.findIssueByKey(repository.getId(), searchKey))
                    .map(key -> new SVNRepositoryIssue(
                                    repository,
                                    configuredIssueService.getIssue(key)
                            )
                    );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, SVNRevisionInfo basicInfo) {
        return SVNServiceUtils.createChangeLogRevision(
                repository,
                basicInfo.getPath(),
                0,
                basicInfo.getRevision(),
                basicInfo.getMessage(),
                basicInfo.getAuthor(),
                basicInfo.getDateTime()
        );
    }

    @Override
    public void forEachConfiguredBranch(
            Predicate<SVNProjectConfigurationProperty> projectConfigurationPredicate,
            BiConsumer<Branch, SVNBranchConfigurationProperty> branchConsumer) {
        // Loops over all authorised branches
        for (Project project : structureService.getProjectList()) {
            // Filter on SVN configuration: must be present and equal to the one the revision info is looked into
            Property<SVNProjectConfigurationProperty> projectSvnConfig = propertyService.getProperty(project, SVNProjectConfigurationPropertyType.class);
            if (!projectSvnConfig.isEmpty() && projectConfigurationPredicate.test(projectSvnConfig.getValue())) {
                structureService.getBranchesForProject(project.getId()).stream()
                        .filter(branch -> propertyService.hasProperty(branch, SVNBranchConfigurationPropertyType.class))
                        .forEach(branch -> {
                            // Branch configuration
                            SVNBranchConfigurationProperty branchConfiguration = propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class).getValue();
                            // OK
                            branchConsumer.accept(branch, branchConfiguration);
                        });
            }
        }
    }

    @Override
    public SVNSyncInfo getSyncInfo(ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        return new SVNSyncInfo(
                branch,
                propertyService.getProperty(branch, SVNSyncPropertyType.class).getValue()
        );
    }

    @Override
    public Optional<SVNRepository> getSVNRepository(Branch branch) {
        // Gets the SVN project configuration property
        Property<SVNProjectConfigurationProperty> projectConfiguration = propertyService.getProperty(
                branch.getProject(),
                SVNProjectConfigurationPropertyType.class
        );
        if (projectConfiguration.isEmpty()) {
            return Optional.empty();
        } else {
            SVNConfiguration configuration = projectConfiguration.getValue().getConfiguration();
            return Optional.of(
                    getRepository(configuration.getName())
            );
        }
    }

    @Override
    public SVNRepository getRequiredSVNRepository(Branch branch) {
        return getSVNRepository(branch)
                .orElseThrow(() ->
                                new MissingSVNProjectConfigurationException(branch.getProject().getName())
                );
    }

    @Override
    public List<String> getBranches(Branch branch) {
        Property<SVNProjectConfigurationProperty> svnProperty = propertyService.getProperty(
                branch.getProject(),
                SVNProjectConfigurationPropertyType.class
        );
        if (svnProperty.isEmpty()) {
            return Collections.emptyList();
        } else {
            SVNRepository repository = getSVNRepository(branch).get();
            String projectPath = svnProperty.getValue().getProjectPath();
            String branchesDir;
            if (projectPath.endsWith("/trunk")) {
                branchesDir = projectPath.replace("/trunk", "/branches");
            } else {
                branchesDir = projectPath + "/branches";
            }
            return svnClient.getBranches(
                    repository,
                    SVNUtils.toURL(repository.getUrl(branchesDir))
            );
        }
    }

    @Override
    public ConnectionResult test(SVNConfiguration configuration) {
        // No trailing slash
        String url = configuration.getUrl();
        if (StringUtils.endsWith(url, "/")) {
            throw new SVNURLFormatException(
                    "The Subversion URL must not end with a slash: %s",
                    url
            );
        }
        if (svnConfigProperties.isTest()) {
            //noinspection unused
            try (Transaction tx = transactionService.start()) {
                // Creates a repository
                SVNRepository repository = SVNRepository.of(
                        0,
                        configuration,
                        null
                );
                // Configuration URL
                SVNURL svnurl = SVNUtils.toURL(configuration.getUrl());
                // Connection to the root
                if (!svnClient.exists(
                        repository,
                        svnurl,
                        SVNRevision.HEAD
                )) {
                    return ConnectionResult.error(configuration.getUrl() + " does not exist.");
                }
                // Gets base info
                SVNInfo info = svnClient.getInfo(repository, svnurl, SVNRevision.HEAD);
                // Checks the repository root
                if (!Objects.equals(
                        info.getRepositoryRootURL(),
                        svnurl
                )) {
                    return ConnectionResult.error(configuration.getUrl() + " must be the root of the repository.");
                }
                // OK
                return ConnectionResult.ok();
            } catch (Exception ex) {
                return ConnectionResult.error(ex.getMessage());
            }
        } else {
            logger.warn("[svn] SVN configuration URL checks have been disabled.");
            return ConnectionResult.ok();
        }
    }

    @Override
    public Optional<String> download(ID branchId, String path) {
        Branch branch = structureService.getBranch(branchId);
        // Security check
        securityService.checkProjectFunction(branch, ProjectConfig.class);
        // If project configured...
        Optional<SVNRepository> oSvnRepository = getSVNRepository(branch);
        if (oSvnRepository.isPresent()) {
            // SVN Branch configuration
            Optional<SVNBranchConfigurationProperty> oSvnBranchConfigurationProperty = propertyService.getProperty(
                    branch,
                    SVNBranchConfigurationPropertyType.class
            ).option();
            if (oSvnBranchConfigurationProperty.isPresent()) {
                String pathInBranch = StringUtils.stripEnd(oSvnBranchConfigurationProperty.get().getBranchPath(), "/")
                        + "/"
                        + StringUtils.stripStart(path, "/");
                try (Transaction ignored = transactionService.start()) {
                    return svnClient.download(oSvnRepository.get(), pathInBranch);
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public TCopyEvent getLastCopyEvent(int id, String tagPath, long maxValue) {
        return eventDao.getLastCopyEvent(id, tagPath, maxValue);
    }

    @Override
    public Optional<String> getTagPathForTagName(SVNRepository svnRepository, String branchPath, String tagName) {
        return getBasePath(svnRepository, branchPath)
                .map(basePath -> basePath + "/tags/" + tagName);
    }

    private Optional<String> getBasePath(SVNRepository svnRepository, String branchPath) {
        return svnClient.getBasePath(svnRepository, branchPath);
    }

    @Override
    public SVNLocation getFirstCopyAfter(SVNRepository repository, SVNLocation location) {
        return eventDao.getFirstCopyAfter(repository.getId(), location);
    }

}
