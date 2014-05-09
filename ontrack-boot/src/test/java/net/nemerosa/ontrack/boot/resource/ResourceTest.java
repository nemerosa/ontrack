package net.nemerosa.ontrack.boot.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Test;

import java.util.Arrays;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;

public class ResourceTest {

    public static Project project() {
        return new Project("1", "PRJ", "Project");
    }

    @Test
    public void to_json_only_data() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .end(),
                Resource.of(branch)
        );
    }

    @Test
    public void to_json_self_link() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .with("self", "http://host/branches/b")
                        .end(),
                Resource.of(branch).self("http://host/branches/b")
        );
    }

    @Test
    public void to_json_other_link() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .with("self", "http://host/branches/b")
                        .with("info", object()
                                        .with("href", "http://host/branches/b/info")
                                        .end()
                        )
                        .end(),
                Resource.of(branch)
                        .self("http://host/branches/b")
                        .link("info", "http://host/branches/b/info")
        );
    }

    @Test
    public void to_json_link_with_supplier() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .with("self", "http://host/branches/b")
                        .with("project", object()
                                        .with("href", "http://host/branches/b/project")
                                        .end()
                        )
                        .end(),
                Resource.of(branch)
                        .self("http://host/branches/b")
                        .link("project", "http://host/branches/b/project", () -> Resource.of(new Project("p", "P1", "Project 1")))
        );
    }

    @Test
    public void to_json_link_with_simple_resource() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .with("self", "http://host/branches/b")
                        .with("project", object()
                                        .with("href", "http://host/branches/b/project")
                                        .end()
                        )
                        .end(),
                Resource.of(branch)
                        .self("http://host/branches/b")
                        .link("project", "http://host/branches/b/project", () -> Resource.of(new Project("p", "P1", "Project 1")))
        );
    }

    @Test
    public void to_json_link_with_resource() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .with("self", "http://host/branches/b")
                        .with("project", object()
                                        .with("id", "p")
                                        .with("name", "P1")
                                        .with("description", "Project 1")
                                        .with("self", "http://host/projects/p")
                                        .end()
                        )
                        .end(),
                Resource.of(branch)
                        .self("http://host/branches/b")
                        .link(
                                "project",
                                "http://host/branches/b/project",
                                Resource.of(new Project("p", "P1", "Project 1"))
                                        .self("http://host/projects/p")
                        )
        );
    }

    @Test
    public void to_json_link_with_resource_list() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1", project());
        TestUtils.assertJsonWrite(
                object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .with("self", "http://host/branches/b")
                        .with("project", object()
                                        .with("href", "http://host/branches/b/project")
                                        .end()
                        )
                        .with("builds", object()
                                        .with("collection", array()
                                                .with(object()
                                                                .with("id", "b11")
                                                                .with("name", "11")
                                                                .with("description", "Build 11")
                                                                .with("self", "http://host/builds/b11")
                                                                .with("branch", object()
                                                                                .with("href", "http://host/branches/b")
                                                                                .end()
                                                                )
                                                                .end()
                                                )
                                                .with(object()
                                                                .with("id", "b12")
                                                                .with("name", "12")
                                                                .with("description", "Build 12")
                                                                .with("self", "http://host/builds/b12")
                                                                .with("branch", object()
                                                                                .with("href", "http://host/branches/b")
                                                                                .end()
                                                                )
                                                                .end()
                                                )
                                                .end())
                                        .with("self", "http://host/branches/b/builds")
                                        .end()
                        )
                        .end(),
                Resource.of(branch)
                        .self("http://host/branches/b")
                        .link("project", "http://host/branches/b/project", () -> Resource.of(new Project("p", "P1", "Project 1")))
                        .link(
                                "builds",
                                "http://host/branches/b/builds",
                                Resource.of(
                                        Arrays.asList(
                                                Resource.of(new Build("b11", "11", "Build 11", null, null))
                                                        .link("self", "http://host/builds/b11")
                                                        .link("branch", "http://host/branches/b"),
                                                Resource.of(new Build("b12", "12", "Build 12", null, null))
                                                        .link("self", "http://host/builds/b12")
                                                        .link("branch", "http://host/branches/b")
                                        )
                                ).link("self", "http://host/branches/b/builds")
                        )
        );
    }

}
