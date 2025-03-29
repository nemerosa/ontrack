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

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE_OBJECT;

public class ResourcesTest extends AbstractResourceTest {

    @Test
    public void to_json() throws JsonProcessingException {
        Resources<Dummy> collection = Resources.of(
                Arrays.asList(
                        new Dummy("1"),
                        new Dummy("2")
                ),
                URI.create("http://host/dummy")
        );
        assertResourceJson(
                mapper,
                object()
                        .with("_self", "http://host/dummy")
                        .with("resources", array()
                                .with(object()
                                        .with("version", "1")
                                        .end())
                                .with(object()
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
                Branch.of(project, new NameDescription("B1", "Branch 1")).withSignature(SIGNATURE),
                Branch.of(project, new NameDescription("B2", "Branch 2")).withSignature(SIGNATURE)
        );
        Resources<Branch> resourceCollection = Resources.of(
                branches,
                URI.create("urn:branch")
        );

        assertResourceJson(
                mapper,
                object()
                        .with("_self", "urn:branch")
                        .with("resources", array()
                                .with(object()
                                        .with("id", 0)
                                        .with("name", "B1")
                                        .with("description", "Branch 1")
                                        .with("disabled", false)
                                        .with("signature", SIGNATURE_OBJECT)
                                        .end())
                                .with(object()
                                        .with("id", 0)
                                        .with("name", "B2")
                                        .with("description", "Branch 2")
                                        .with("disabled", false)
                                        .with("signature", SIGNATURE_OBJECT)
                                        .end())
                                .end())
                        .end(),
                resourceCollection,
                Resources.class
        );
    }

}
