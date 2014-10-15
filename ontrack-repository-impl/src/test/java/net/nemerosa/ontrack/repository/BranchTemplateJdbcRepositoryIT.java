package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BranchTemplateJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private BranchTemplateJdbcRepository repository;

    private Branch branch;

    @Before
    public void create_branch() {
        // Creates a project
        Project project = structureRepository.newProject(Project.of(nameDescription()));
        // Creates a branch for this project
        branch = structureRepository.newBranch(Branch.of(project, nameDescription()));
    }

    @Test
    public void save_template_definition() {
        TemplateDefinition templateDefinition = new TemplateDefinition(
                Arrays.asList(
                        new TemplateParameter(
                                "JENKINS_JOB_NAME",
                                "Jenkins job for the branch",
                                "${branchName.toUpperCase()}"
                        ),
                        new TemplateParameter(
                                "SCM_BRANCH",
                                "SCM branch name",
                                "${branchName}"
                        )
                ),
                new ServiceConfiguration(
                        "fixedList",
                        JsonUtils.stringArray("1.0", "1.1")
                ),
                TemplateSynchronisationAbsencePolicy.DELETE,
                10
        );
        // Call
        repository.setTemplateDefinition(
                branch.getId(),
                templateDefinition
        );
        // Gets the template definition back
        Optional<TemplateDefinition> readDefinition = repository.getTemplateDefinition(branch.getId());
        // Checks the template definition
        assertTrue(readDefinition.isPresent());
        assertEquals(
                templateDefinition,
                readDefinition.get()
        );
    }

    @Test
    public void save_template_instance() {
        // Branch as template definition
        // TODO Does it not need to be an actual template?
        Branch template = do_create_branch();

        // Branch as template instance
        Branch instance = do_create_branch();

        // Template instance
        TemplateInstance templateInstance = new TemplateInstance(
                template.getId(),
                Arrays.asList(
                        new TemplateParameterValue("SCM_BRANCH", "ontrack-40"),
                        new TemplateParameterValue("JENKINS_JOB", "ONTRACK-40")
                )
        );

        // Saving the instance
        repository.setTemplateInstance(instance.getId(), templateInstance);

        // Is template instance?
        assertTrue(repository.isTemplateInstance(instance.getId()));

        // Gets the template instance back
        Optional<TemplateInstance> retrievedInstance = repository.getTemplateInstance(instance.getId());
        assertTrue(retrievedInstance.isPresent());
        assertEquals(
                new TemplateInstance(
                        template.getId(),
                        Arrays.asList(
                                new TemplateParameterValue("JENKINS_JOB", "ONTRACK-40"),
                                new TemplateParameterValue("SCM_BRANCH", "ontrack-40")
                        )
                ),
                retrievedInstance.get()
        );
    }

}
