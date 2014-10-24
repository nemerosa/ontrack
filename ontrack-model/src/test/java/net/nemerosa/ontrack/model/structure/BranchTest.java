package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class BranchTest {

    @Test
    public void collection_without_projects() throws JsonProcessingException {
        Project project = Project.of(new NameDescription("PRJ", "Project"));
        List<Branch> branches = Arrays.asList(
                Branch.of(project, new NameDescription("B1", "Branch 1")),
                Branch.of(project, new NameDescription("B2", "Branch 2"))
        );

        assertJsonWrite(
                array()
                        .with(object()
                                .with("id", 0)
                                .with("name", "B1")
                                .with("description", "Branch 1")
                                .with("disabled", false)
                                .with("type", "CLASSIC")
                                .end())
                        .with(object()
                                .with("id", 0)
                                .with("name", "B2")
                                .with("description", "Branch 2")
                                .with("disabled", false)
                                .with("type", "CLASSIC")
                                .end())
                        .end(),
                branches,
                List.class
        );
    }

    @Test
    public void branch_with_project() throws JsonProcessingException {
        Project project = Project.of(new NameDescription("PRJ", "Project"));
        Branch branch = Branch.of(project, new NameDescription("B", "Branch"));

        assertJsonWrite(
                object()
                        .with("id", 0)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "CLASSIC")
                        .with("project", object()
                                .with("id", 0)
                                .with("name", "PRJ")
                                .with("description", "Project")
                                .with("disabled", false)
                                .end())
                        .end(),
                branch,
                Branch.class
        );
    }

}
