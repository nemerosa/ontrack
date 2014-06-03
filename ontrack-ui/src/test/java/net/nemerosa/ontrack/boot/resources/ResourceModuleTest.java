package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import org.junit.Before;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class ResourceModuleTest {

    private ObjectMapper mapper;

    @Before
    public void before() {
        mapper = ObjectMapperFactory.create();
        mapper.registerModule(new CoreResourceModule(new MockURIBuilder()));
    }

    @Test
    public void promotion_level_image_link_and_ignored_branch() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1));
        PromotionLevel pl = PromotionLevel.of(b, new NameDescription("PL", "Promotion level")).withId(ID.of(1));
        // Serialization
        assertJsonWrite(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "PL")
                        .with("description", "Promotion level")
                        .with("image", false)
                        .with("imageLink", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelImage_:1")
                        .end(),
                pl,
                Branch.class
        );
    }

    @Test
    public void promotion_level_image_link_and_include_branch() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1));
        PromotionLevel pl = PromotionLevel.of(b, new NameDescription("PL", "Promotion level")).withId(ID.of(1));
        // Serialization
        assertJsonWrite(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "PL")
                        .with("description", "Promotion level")
                        .with("image", false)
                        .with("branch", object()
                                .with("id", 1)
                                .with("name", "B")
                                .with("description", "Branch")
                                .with("project", object()
                                        .with("id", 1)
                                        .with("name", "P")
                                        .with("description", "Project")
                                        .end())
                                .end())
                        .with("imageLink", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelImage_:1")
                        .end(),
                pl,
                PromotionLevel.class
        );
    }

}
