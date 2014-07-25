package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.client.GitClient;
import net.nemerosa.ontrack.extension.git.client.GitClientFactory;
import net.nemerosa.ontrack.extension.git.client.GitTag;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
public class GitServiceImpl implements GitService, JobProvider {

    private final StructureService structureService;
    private final Collection<GitConfigurator> configurators;
    private final GitClientFactory gitClientFactory;
    private final JobQueueService jobQueueService;
    private final SecurityService securityService;

    @Autowired
    public GitServiceImpl(
            StructureService structureService,
            Collection<GitConfigurator> configurators,
            GitClientFactory gitClientFactory,
            JobQueueService jobQueueService, SecurityService securityService) {
        this.structureService = structureService;
        this.configurators = configurators;
        this.gitClientFactory = gitClientFactory;
        this.jobQueueService = jobQueueService;
        this.securityService = securityService;
    }

    @Override
    public Collection<Job> getJobs() {
        Collection<Job> jobs = new ArrayList<>();
        for (Project project : structureService.getProjectList()) {
            for (Branch branch : structureService.getBranchesForProject(project.getId())) {
                GitConfiguration configuration = getBranchConfiguration(branch);
                if (configuration.isValid() && configuration.getIndexationInterval() > 0) {
                    jobs.add(createIndexationJob(configuration));
                }
            }
        }
        return jobs;
    }

    @Override
    public boolean isBranchConfiguredForGit(Branch branch) {
        return getBranchConfiguration(branch).isValid();
    }

    @Override
    public Ack launchBuildSync(ID branchId) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Gets its configuration
        GitConfiguration configuration = getBranchConfiguration(branch);
        // If valid, launches a job
        if (configuration.isValid()) {
            return jobQueueService.queue(createBuildSyncJob(branch, configuration));
        }
        // Else, nothing has happened
        else {
            return Ack.NOK;
        }
    }

    private GitConfiguration getBranchConfiguration(Branch branch) {
        // Empty configuration
        GitConfiguration configuration = GitConfiguration.empty();
        // Configurators{
        for (GitConfigurator configurator : configurators) {
            configuration = configurator.configure(configuration, branch);
        }
        // Unique name
        if (StringUtils.isNotBlank(configuration.getRemote())) {
            configuration = configuration.withName(
                    format(
                            "%s/%s/%s",
                            branch.getProject().getName(),
                            branch.getName(),
                            configuration.getRemote()
                    )
            );
        }
        // OK
        return configuration;
    }

    private Job createBuildSyncJob(Branch branch, GitConfiguration configuration) {
        return new Job() {
            @Override
            public String getCategory() {
                return "GitBuildTagSync";
            }

            @Override
            public String getId() {
                return String.valueOf(branch.getId());
            }

            @Override
            public String getDescription() {
                return format(
                        "Git build/tag synchro for branch %s/%s",
                        branch.getProject().getName(),
                        branch.getName()
                );
            }

            @Override
            public int getInterval() {
                return configuration.getIndexationInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(info -> buildSync(branch, configuration, info));
            }
        };
    }

    private Job createIndexationJob(GitConfiguration config) {
        return new Job() {
            @Override
            public String getCategory() {
                return "GitIndexation";
            }

            @Override
            public String getId() {
                return config.getName();
            }

            @Override
            public String getDescription() {
                return format(
                        "Git indexation for %s",
                        config.getName()
                );
            }

            @Override
            public int getInterval() {
                return config.getIndexationInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(
                        info -> index(config, info)
                );
            }
        };
    }

    private void buildSync(Branch branch, GitConfiguration configuration, JobInfoListener info) {
        info.post(format("Git build/tag sync for %s/%s", branch.getProject().getName(), branch.getName()));
        // Gets the branch Git client
        GitClient gitClient = gitClientFactory.getClient(configuration);
        // Makes sure of synchronization
        info.post("Synchronizing before importing");
        gitClient.sync(info::post);
        // Gets the list of tags
        info.post("Getting list of tags");
        Collection<GitTag> tags = gitClient.getTags();
        // Pattern for the tags
        // TODO Make the pattern configurable at branch level using a property
        final Pattern tagPattern = Pattern.compile("(.*)");
        // Creates the builds
        info.post("Creating builds from tags");
        for (GitTag tag : tags) {
            String tagName = tag.getName();
            // Filters the tags according to the branch tag pattern
            Matcher matcher = tagPattern.matcher(tagName);
            if (matcher.matches()) {
                // Build name
                info.post(format("Creating build for tag %s", tagName));
                String buildName = matcher.group(1);
                info.post(format("Build %s from tag %s", buildName, tagName));
                // Existing build?
                boolean createBuild;
                Optional<Build> build = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
                if (build.isPresent()) {
                    // TODO Build override policy can be configurable at branch level using a property (same than for the tag pattern)
                    boolean override = false;
                    //noinspection ConstantConditions
                    if (override) {
                        // Deletes the build
                        info.post(format("Deleting existing build %s", buildName));
                        structureService.deleteBuild(build.get().getId());
                        createBuild = true;
                    } else {
                        // Keeps the build
                        info.post(format("Build %s already exists", buildName));
                        createBuild = false;
                    }
                } else {
                    createBuild = true;
                }
                // Actual creation
                if (createBuild) {
                    info.post(format("Creating build %s from tag %s", buildName, tagName));
                    structureService.newBuild(
                            Build.of(
                                    branch,
                                    new NameDescription(
                                            buildName,
                                            "Imported from Git tag " + tagName
                                    ),
                                    securityService.getCurrentSignature().withTime(
                                            tag.getTime()
                                    )
                            )
                    );
                }
            }
        }
    }

    private void index(GitConfiguration config, JobInfoListener info) {
        info.post(format("Git sync for %s", config.getName()));
        // Gets the client for this configuration
        GitClient client = gitClientFactory.getClient(config);
        // Launches the synchronisation
        client.sync(info::post);
    }

}
