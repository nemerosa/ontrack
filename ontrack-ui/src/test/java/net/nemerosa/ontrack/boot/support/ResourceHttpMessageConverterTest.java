package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.boot.resources.CoreResourceModule;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.test.TestUtils;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceHttpMessageConverterTest {

    private ResourceHttpMessageConverter converter;

    @Before
    public void before() {
        SecurityService securityService = mock(SecurityService.class);
        converter = new ResourceHttpMessageConverter(
                new MockURIBuilder(), securityService,
                Arrays.asList(new CoreResourceModule())
        );
    }

    @Test
    public void branch() throws IOException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Projet créé")).withId(ID.of(1));
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1));
        // Message
        HttpOutputMessage message = mock(HttpOutputMessage.class);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(output);
        // Serialization
        converter.writeInternal(b, message);

        // Content
        String json = new String(output.toByteArray(), "UTF-8");

        // Parsing
        JsonNode node = ObjectMapperFactory.create().readTree(json);

        // Check
        TestUtils.assertJsonEquals(
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("project", object()
                                        .with("id", 1)
                                        .with("name", "P")
                                        .with("description", "Projet créé")
                                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                                        .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                                        .with("_branchStatusViews", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getBranchStatusViews:1")
                                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:PROJECT,1")
                                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                                        .end()
                        )
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranch:1")
                        .with("_project", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_promotionLevels", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelListForBranch:1")
                        .with("_validationStamps", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampListForBranch:1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:BRANCH,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:BRANCH,1")
                        .with("_status", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchStatusView:1")
                        .with("_view", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#buildView:1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:BRANCH,1")
                        .with("_buildFilterResources", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#buildFilters:1")
                        .with("_buildFilterForms", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#buildFilterForms:1")
                        .with("_buildFilterSave", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#createFilter:1,")
                        .end(),
                node
        );
    }

}
