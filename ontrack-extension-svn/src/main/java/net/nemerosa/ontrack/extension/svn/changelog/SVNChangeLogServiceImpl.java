package net.nemerosa.ontrack.extension.svn.changelog;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.scm.changelog.AbstractSCMChangeLogService;
import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.svn.*;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.model.SVNHistory;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SVNChangeLogServiceImpl extends AbstractSCMChangeLogService implements SVNChangeLogService {

    private final PropertyService propertyService;
    private final SVNConfigurationService configurationService;
    private final SVNRepositoryDao repositoryDao;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNClient svnClient;
    private final TransactionService transactionService;
    private final SecurityService securityService;

    @Autowired
    public SVNChangeLogServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            SVNConfigurationService configurationService, SVNRepositoryDao repositoryDao,
            IssueServiceRegistry issueServiceRegistry,
            SVNClient svnClient, TransactionService transactionService, SecurityService securityService) {
        super(structureService);
        this.propertyService = propertyService;
        this.configurationService = configurationService;
        this.repositoryDao = repositoryDao;
        this.issueServiceRegistry = issueServiceRegistry;
        this.svnClient = svnClient;
        this.transactionService = transactionService;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public SVNChangeLog changeLog(BuildDiffRequest request) {
        try (Transaction ignored = transactionService.start()) {
            Branch branch = structureService.getBranch(request.getBranch());
            SVNRepository svnRepository = getSVNRepository(branch);
            return new SVNChangeLog(
                    branch,
                    svnRepository,
                    getSCMBuildView(svnRepository, request.getFrom()),
                    getSCMBuildView(svnRepository, request.getTo())
            );
        }
    }

    protected SCMBuildView<SVNHistory> getSCMBuildView(SVNRepository svnRepository, ID buildId) {
        // Gets the build view
        BuildView buildView = getBuildView(buildId);
        // Gets the history for the build
        SVNHistory history = getBuildSVNHistory(svnRepository, buildView.getBuild());
        // OK
        return new SCMBuildView<>(buildView, history);
    }

    protected SVNHistory getBuildSVNHistory(SVNRepository svnRepository, Build build) {
        // Gets the build path for the branch
        String svnBuildPath = getSVNBuildPath(build);
        // Gets the history from the SVN client
        return svnClient.getHistory(svnRepository, svnBuildPath);
    }

    protected String getSVNBuildPath(Build build) {
        // Gets the build path property value
        Property<SVNBranchConfigurationProperty> branchConfiguration = propertyService.getProperty(
                build.getBranch(),
                SVNBranchConfigurationPropertyType.class
        );
        if (branchConfiguration.isEmpty()) {
            throw new MissingSVNBranchConfigurationException(build.getBranch().getName());
        } else {
            // Gets the build path definition
            String buildPathDefinition = branchConfiguration.getValue().getBuildPath();
            // Expands the build path
            return expandBuildPath(buildPathDefinition, build);
        }
    }

    protected String expandBuildPath(String buildPathDefinition, Build build) {
        // Pattern
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(buildPathDefinition);
        StringBuffer path = new StringBuffer();
        while (matcher.find()) {
            String replacement = expandBuildPathExpression(matcher.group(1), build);
            matcher.appendReplacement(path, replacement);
        }
        matcher.appendTail(path);
        // TODO Property expansion
        // OK
        return path.toString();
    }

    protected String expandBuildPathExpression(String expression, Build build) {
        if ("build".equals(expression)) {
            return build.getName();
        } else {
            throw new UnknownBuildPathExpression(expression);
        }
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
            return securityService.asAdmin(() -> SVNRepository.of(
                    repositoryDao.getOrCreateByName(configuration.getName()),
                    // The configuration contained in the property's configuration is obfuscated
                    // and the original one must be loaded
                    configurationService.getConfiguration(configuration.getName()),
                    issueServiceRegistry.getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier())
            ));
        }
    }

}
