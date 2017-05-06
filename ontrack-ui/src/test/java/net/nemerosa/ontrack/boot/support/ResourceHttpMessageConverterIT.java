package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.test.TestUtils;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import net.nemerosa.ontrack.ui.resource.DefaultResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE_OBJECT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceHttpMessageConverterIT extends AbstractServiceTestSupport {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private Collection<ResourceDecorator<?>> decorators;

    private ResourceHttpMessageConverter converter;

    @Before
    public void before() {
        converter = new ResourceHttpMessageConverter(
                new MockURIBuilder(), securityService,
                Collections.singletonList(new DefaultResourceModule(decorators))
        );
    }

    @Test
    public void branch() throws IOException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Projet créé")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withSignature(SIGNATURE);
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
                        .with("disabled", false)
                        .with("type", "CLASSIC")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Projet créé")
                                .with("disabled", false)
                                .with("signature", SIGNATURE_OBJECT)
                                .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                                .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                                .with("_branchStatusViews", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getBranchStatusViews:1")
                                .with("_buildSearch", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#buildSearchForm:1")
                                .with("_buildDiffActions", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#buildDiffActions:1")
                                .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:PROJECT,1")
                                .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                                .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                                .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                                .with("_page", "urn:test:#:entity:PROJECT:1")
                                .end()
                        )
                        .with("signature", SIGNATURE_OBJECT)
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranch:1")
                        .with("_project", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_promotionLevels", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelListForBranch:1")
                        .with("_validationStamps", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampListForBranch:1")
                        .with("_validationStampViews", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampViewListForBranch:1")
                        .with("_allValidationStampFilters", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampFilterController#getAllBranchValidationStampFilters:1")
                        .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:BRANCH,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:BRANCH,1")
                        .with("_status", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchStatusView:1")
                        .with("_view", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#buildView:1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:BRANCH,1")
                        .with("_buildFilterResources", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#buildFilters:1")
                        .with("_buildFilterForms", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#buildFilterForms:1")
                        .with("_buildFilterSave", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#createFilter:1,")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:BRANCH,1,0,10")
                        .with("_page", "urn:test:#:entity:BRANCH:1")
                        .end(),
                node
        );
    }

    @Test
    public void branch_disable_granted_for_automation() throws Exception {
        // Objects
        Project p = Project.of(new NameDescription("P", "Projet créé")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        // Message
        HttpOutputMessage message = mock(HttpOutputMessage.class);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(output);
        // Serialization
        asGlobalRole("AUTOMATION").execute(() -> {
                    try {
                        converter.writeInternal(b, message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        // Content
        String json = new String(output.toByteArray(), "UTF-8");

        // Parsing
        JsonNode node = ObjectMapperFactory.create().readTree(json);

        // Disable link
        assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.BranchController#disableBranch:1", node.path("_disable").asText());
    }

    @Test
    public void branch_enable_granted_for_automation() throws Exception {
        // Objects
        Project p = Project.of(new NameDescription("P", "Projet créé")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withDisabled(true)
                .withSignature(SIGNATURE);
        // Message
        HttpOutputMessage message = mock(HttpOutputMessage.class);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(output);
        // Serialization
        asGlobalRole("AUTOMATION").execute(() -> {
                    try {
                        converter.writeInternal(b, message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        // Content
        String json = new String(output.toByteArray(), "UTF-8");

        // Parsing
        JsonNode node = ObjectMapperFactory.create().readTree(json);

        // Enable link
        assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.BranchController#enableBranch:1", node.path("_enable").asText());
    }

}
