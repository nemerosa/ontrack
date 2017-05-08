package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import net.nemerosa.ontrack.ui.resource.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE;
import static net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE_OBJECT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreResourceModuleTest {

    private ResourceObjectMapper mapper;
    private SecurityService securityService;
    private StructureService structureService;
    private ProjectFavouriteService projectFavouriteService;

    @Before
    public void before() {
        securityService = mock(SecurityService.class);
        structureService = mock(StructureService.class);
        ResourceDecorationContributorService resourceDecorationContributorService = mock(ResourceDecorationContributorService.class);
        projectFavouriteService = mock(ProjectFavouriteService.class);
        mapper = new ResourceObjectMapperFactory().resourceObjectMapper(
                Collections.singletonList(
                        new DefaultResourceModule(
                                Arrays.asList(
                                        new ConnectedAccountResourceDecorator(),
                                        new ProjectResourceDecorator(resourceDecorationContributorService, projectFavouriteService),
                                        new BranchResourceDecorator(resourceDecorationContributorService, structureService),
                                        new PromotionLevelResourceDecorator(),
                                        new ValidationStampResourceDecorator(),
                                        new BuildResourceDecorator(resourceDecorationContributorService),
                                        new PromotionRunResourceDecorator(),
                                        new ValidationRunResourceDecorator(),
                                        new BuildFilterResourceDecorator(),
                                        new AccountResourceDecorator(),
                                        new AccountGroupResourceDecorator(),
                                        new GlobalPermissionResourceDecorator(),
                                        new ProjectPermissionResourceDecorator(),
                                        new JobStatusResourceDecorator(),
                                        new PredefinedValidationStampResourceDecorator()
                                )
                        )
                ),
                new DefaultResourceContext(new MockURIBuilder(), securityService)
        );
    }

    public static void assertResourceJson(ResourceObjectMapper mapper, JsonNode expectedJson, Object o) throws JsonProcessingException {
        assertEquals(
                mapper.getObjectMapper().writeValueAsString(expectedJson),
                mapper.write(o)
        );
    }

    public static void assertResourceJson(ResourceObjectMapper mapper, JsonNode expectedJson, Object o, Class<?> view) throws JsonProcessingException {
        assertEquals(
                mapper.getObjectMapper().writeValueAsString(expectedJson),
                mapper.write(o, view)
        );
    }

    @Test
    public void project_granted_for_update() throws JsonProcessingException {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, ProjectEdit.class)).thenReturn(true);
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "P")
                        .with("description", "Project")
                        .with("disabled", false)
                        .with("signature", SIGNATURE_OBJECT)
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                        .with("_branchStatusViews", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getBranchStatusViews:1")
                        .with("_buildSearch", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#buildSearchForm:1")
                        .with("_buildDiffActions", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#buildDiffActions:1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_update", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProject:1,")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_disable", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#disableProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        );
    }

    @Test
    public void project_not_favourite() throws JsonProcessingException {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        when(securityService.isLogged()).thenReturn(true);
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "P")
                        .with("description", "Project")
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
                        .with("_favourite", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#favouriteProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        );
    }

    @Test
    public void project_no_favourite_link_if_not_logged() throws JsonProcessingException {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "P")
                        .with("description", "Project")
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
                        .end(),
                p
        );
    }

    @Test
    public void project_favourite() throws JsonProcessingException {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        when(securityService.isLogged()).thenReturn(true);
        when(projectFavouriteService.isProjectFavourite(p)).thenReturn(true);
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "P")
                        .with("description", "Project")
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
                        .with("_unfavourite", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#unfavouriteProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        );
    }

    @Test
    public void project_granted_for_update_and_disabled() throws JsonProcessingException {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withDisabled(true)
                .withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, ProjectEdit.class)).thenReturn(true);
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "P")
                        .with("description", "Project")
                        .with("disabled", true)
                        .with("signature", SIGNATURE_OBJECT)
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                        .with("_branchStatusViews", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getBranchStatusViews:1")
                        .with("_buildSearch", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#buildSearchForm:1")
                        .with("_buildDiffActions", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#buildDiffActions:1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_update", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProject:1,")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_enable", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#enableProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        );
    }

    @Test
    public void branch_no_build_granted_for_template_definition() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, BranchTemplateMgt.class)).thenReturn(true);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "CLASSIC")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                        .with("_templateDefinition", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getTemplateDefinition:1")
                        .with("_templateInstanceConnect", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#connectTemplateInstance:1")
                        .with("_page", "urn:test:#:entity:BRANCH:1")
                        .end(),
                b
        );
    }

    @Test
    public void branch_instance_for_template_definition() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withType(BranchType.TEMPLATE_INSTANCE)
                .withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, BranchTemplateMgt.class)).thenReturn(true);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "TEMPLATE_INSTANCE")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                        .with("_templateInstance", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getTemplateInstance:1")
                        .with("_templateInstanceDisconnect", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#disconnectTemplateInstance:1")
                        .with("_templateInstanceSync", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#syncTemplateInstance:1")
                        .with("_page", "urn:test:#:entity:BRANCH:1")
                        .end(),
                b
        );
    }

    @Test
    public void branch_with_builds_for_template_definition() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        when(structureService.getBuildCount(b)).thenReturn(1);
        when(securityService.isProjectFunctionGranted(1, BranchTemplateMgt.class)).thenReturn(true);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "CLASSIC")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                        .with("_templateInstanceConnect", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#connectTemplateInstance:1")
                        .with("_page", "urn:test:#:entity:BRANCH:1")
                        .end(),
                b
        );
    }

    @Test
    public void branch_definition_granted() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withType(BranchType.TEMPLATE_DEFINITION)
                .withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, BranchTemplateMgt.class)).thenReturn(true);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "TEMPLATE_DEFINITION")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                        .with("_templateDefinition", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getTemplateDefinition:1")
                        .with("_templateSync", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#syncTemplateDefinition:1")
                        .with("_templateInstanceCreate", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#singleTemplateInstanceForm:1")
                        .with("_page", "urn:test:#:entity:BRANCH:1")
                        .end(),
                b
        );
    }

    @Test
    public void branch_sync_granted_for_controller() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withType(BranchType.TEMPLATE_DEFINITION)
                .withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, BranchTemplateSync.class)).thenReturn(true);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "TEMPLATE_DEFINITION")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                        .with("_templateSync", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#syncTemplateDefinition:1")
                        .with("_templateInstanceCreate", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#singleTemplateInstanceForm:1")
                        .with("_page", "urn:test:#:entity:BRANCH:1")
                        .end(),
                b
        );
    }

    @Test
    public void branch_definition_not_granted() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1))
                .withType(BranchType.TEMPLATE_DEFINITION)
                .withSignature(SIGNATURE);
        when(securityService.isProjectFunctionGranted(1, BranchTemplateMgt.class)).thenReturn(false);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "TEMPLATE_DEFINITION")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                b
        );
    }

    @Test
    public void branch_no_grant() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("type", "CLASSIC")
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
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
                b
        );
    }

    @Test
    public void build_view() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE);
        LocalDateTime signatureTime = LocalDateTime.of(2015, 6, 17, 11, 41);
        Build build = Build.of(b, NameDescription.nd("1", "Build 1"), Signature.of(signatureTime, "test")).withId(ID.of(1));
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "1")
                        .with("description", "Build 1")
                        .with("signature", object()
                                .with("time", "2015-06-17T11:41:00Z")
                                .with("user", object().with("name", "test").end())
                                .end()
                        )
                        .with("branch", object()
                                .with("id", 1)
                                .with("name", "B")
                                .with("description", "Branch")
                                .with("disabled", false)
                                .with("type", "CLASSIC")
                                .with("project", object()
                                        .with("id", 1)
                                        .with("name", "P")
                                        .with("description", "Project")
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
                                .end()
                        )
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#getBuild:1")
                        .with("_lastPromotionRuns", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionRunController#getLastPromotionRuns:1")
                        .with("_promotionRuns", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionRunController#getPromotionRuns:1")
                        .with("_validationRuns", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRuns:1")
                        .with("_validationStampRunViews", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationStampRunViews:1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:BUILD,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:BUILD,1")
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:BUILD,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:BUILD,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:BUILD,1,0,10")
                        .with("_previous", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#getPreviousBuild:1")
                        .with("_next", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#getNextBuild:1")
                        .with("_buildLinksFrom", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#getBuildLinksFrom:1")
                        .with("_buildLinksTo", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#getBuildLinksTo:1")
                        .with("_page", "urn:test:#:entity:BUILD:1")
                        .end(),
                build
        );
    }

    @Test
    public void project_not_granted_for_update() throws JsonProcessingException {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE);
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "P")
                        .with("description", "Project")
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
                        .end(),
                p
        );
    }

    @Test
    public void promotion_level_image_link_and_ignored_branch() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE);
        PromotionLevel pl = PromotionLevel.of(b, new NameDescription("PL", "Promotion level")).withId(ID.of(1)).withSignature(SIGNATURE);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "PL")
                        .with("description", "Promotion level")
                        .with("image", false)
                        .with("signature", SIGNATURE_OBJECT)
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevel:1")
                        .with("_branch", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranch:1")
                        .with("_project", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_image", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelImage_:,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROMOTION_LEVEL,1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:PROMOTION_LEVEL,1")
                        .with("_runs", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionRunView:1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROMOTION_LEVEL,1,0,10")
                        .with("_page", "urn:test:#:entity:PROMOTION_LEVEL:1")
                        .end(),
                pl,
                Branch.class
        );
    }

    @Test
    public void promotion_level_image_link_and_include_branch() throws JsonProcessingException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE);
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE);
        PromotionLevel pl = PromotionLevel.of(b, new NameDescription("PL", "Promotion level"))
                .withId(ID.of(1))
                .withSignature(SIGNATURE);
        // Serialization
        assertResourceJson(
                mapper,
                object()
                        .with("id", 1)
                        .with("name", "PL")
                        .with("description", "Promotion level")
                        .with("branch", object()
                                .with("id", 1)
                                .with("name", "B")
                                .with("description", "Branch")
                                .with("disabled", false)
                                .with("type", "CLASSIC")
                                .with("project", object()
                                        .with("id", 1)
                                        .with("name", "P")
                                        .with("description", "Project")
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
                                        .end())
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
                                .end())
                        .with("image", false)
                        .with("signature", SIGNATURE_OBJECT)
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevel:1")
                        .with("_branch", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranch:1")
                        .with("_project", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_image", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelImage_:,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROMOTION_LEVEL,1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:PROMOTION_LEVEL,1")
                        .with("_runs", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionRunView:1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROMOTION_LEVEL,1,0,10")
                        .with("_page", "urn:test:#:entity:PROMOTION_LEVEL:1")
                        .end(),
                pl
        );
    }

    @Test
    public void resource_collection_with_filtering() throws JsonProcessingException {
        Project project = Project.of(new NameDescription("PRJ", "Project")).withId(ID.of(1));
        List<Branch> branches = Arrays.asList(
                Branch.of(project, new NameDescription("B1", "Branch 1")).withId(ID.of(1)).withSignature(SIGNATURE),
                Branch.of(project, new NameDescription("B2", "Branch 2")).withId(ID.of(2)).withSignature(SIGNATURE)
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
                                        .with("id", 1)
                                        .with("name", "B1")
                                        .with("description", "Branch 1")
                                        .with("disabled", false)
                                        .with("type", "CLASSIC")
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
                                        .end())
                                .with(object()
                                        .with("id", 2)
                                        .with("name", "B2")
                                        .with("description", "Branch 2")
                                        .with("disabled", false)
                                        .with("type", "CLASSIC")
                                        .with("signature", SIGNATURE_OBJECT)
                                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranch:2")
                                        .with("_project", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                                        .with("_promotionLevels", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelListForBranch:2")
                                        .with("_validationStamps", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampListForBranch:2")
                                        .with("_validationStampViews", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampViewListForBranch:2")
                                        .with("_allValidationStampFilters", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampFilterController#getAllBranchValidationStampFilters:2")
                                        .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:BRANCH,2")
                                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:BRANCH,2")
                                        .with("_status", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchStatusView:2")
                                        .with("_view", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#buildView:2")
                                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:BRANCH,2")
                                        .with("_buildFilterResources", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#buildFilters:2")
                                        .with("_buildFilterForms", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#buildFilterForms:2")
                                        .with("_buildFilterSave", "urn:test:net.nemerosa.ontrack.boot.ui.BuildFilterController#createFilter:2,")
                                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:BRANCH,2,0,10")
                                        .with("_page", "urn:test:#:entity:BRANCH:2")
                                        .end())
                                .end())
                        .end(),
                resourceCollection
        );
    }

    @Test
    public void account_group_links() throws JsonProcessingException {
        assertResourceJson(
                mapper,
                object()
                        .with("id", 0)
                        .with("name", "Admins")
                        .with("description", "Administrators")
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.AccountController#getGroup:0")
                        .with("_update", "urn:test:net.nemerosa.ontrack.boot.ui.AccountController#getGroupUpdateForm:0")
                        .with("_delete", "urn:test:net.nemerosa.ontrack.boot.ui.AccountController#deleteGroup:0")
                        .end(),
                AccountGroup.of("Admins", "Administrators")
        );
    }

    @Test
    public void promotion_run_delete_granted() throws IOException {
        // Objects
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1)).withType(BranchType.TEMPLATE_DEFINITION);
        PromotionLevel pl = PromotionLevel.of(b, NameDescription.nd("PL", "Promotion Level")).withId(ID.of(1));
        Build build = Build.of(b, NameDescription.nd("1", "Build 1"), Signature.of("test")).withId(ID.of(1));
        PromotionRun run = PromotionRun.of(build, pl, Signature.of("test"), "Run").withId(ID.of(1));
        // Security
        when(securityService.isProjectFunctionGranted(1, PromotionRunDelete.class)).thenReturn(true);
        // Serialization
        JsonNode node = mapper.getObjectMapper().readTree(mapper.write(run));
        // Checks the _delete link is present
        String delete = JsonUtils.get(node, "_delete");
        assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.PromotionRunController#deletePromotionRun:1", delete);
    }

}
