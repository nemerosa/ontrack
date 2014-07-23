package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class GitServiceImpl implements GitService, JobProvider {

    private final StructureService structureService;
    private final Collection<GitConfigurator> configurators;

    @Autowired
    public GitServiceImpl(StructureService structureService, Collection<GitConfigurator> configurators) {
        this.structureService = structureService;
        this.configurators = configurators;
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

    private GitConfiguration getBranchConfiguration(Branch branch) {
        // Empty configuration
        GitConfiguration configuration = GitConfiguration.empty();
        // Configurators{
        for (GitConfigurator configurator : configurators) {
            configuration = configurator.configure(configuration, branch);
        }
        // OK
        return configuration;
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
                return String.format(
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

    private void index(GitConfiguration config, JobInfoListener info) {
        // FIXME Method net.nemerosa.ontrack.extension.git.service.GitServiceImpl.index

    }

}
