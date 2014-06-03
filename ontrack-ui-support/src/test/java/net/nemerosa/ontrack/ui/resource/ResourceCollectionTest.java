package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;

public class ResourceCollectionTest extends AbstractResourceTest {

    @Test
    public void to_json() throws JsonProcessingException {
        ResourceCollection<Dummy> collection = ResourceCollection.of(
                Arrays.asList(
                        Resource.of(new Dummy("1"), URI.create("http://host/dummy/1")),
                        Resource.of(new Dummy("2"), URI.create("http://host/dummy/2"))
                ),
                URI.create("http://host/dummy")
        );
        assertResourceJson(
                mapper,
                object()
                        .with("_self", "http://host/dummy")
                        .with("resources", array()
                                .with(object()
                                        .with("_self", "http://host/dummy/1")
                                        .with("version", "1")
                                        .end())
                                .with(object()
                                        .with("_self", "http://host/dummy/2")
                                        .with("version", "2")
                                        .end())
                                .end())
                        .end(),
                ObjectMapperFactory.create().valueToTree(collection)
        );
    }

    @Test
    public void resource_collection_with_filtering() throws JsonProcessingException {
        Project project = Project.of(new NameDescription("PRJ", "Project"));
        List<Branch> branches = Arrays.asList(
                Branch.of(project, new NameDescription("B1", "Branch 1")),
                Branch.of(project, new NameDescription("B2", "Branch 2"))
        );
        ResourceCollection<Branch> resourceCollection = ResourceCollection.of(
                branches.stream()
                        .map(b -> Resource.of(b, URI.create("urn:branch:" + b.getName())))
                        .collect(Collectors.toList()),
                URI.create("urn:branch")
        );

        assertResourceJson(
                mapper,
                object()
                        .with("_self", "urn:branch")
                        .with("resources", array()
                                .with(object()
                                        .with("_self", "urn:branch:B1")
                                        .with("id", 0)
                                        .with("name", "B1")
                                        .with("description", "Branch 1")
                                        .end())
                                .with(object()
                                        .with("_self", "urn:branch:B2")
                                        .with("id", 0)
                                        .with("name", "B2")
                                        .with("description", "Branch 2")
                                        .end())
                                .end())
                        .end(),
                resourceCollection,
                ResourceCollection.class
        );
    }

}
