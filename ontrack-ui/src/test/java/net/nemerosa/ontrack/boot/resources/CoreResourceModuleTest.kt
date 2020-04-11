package net.nemerosa.ontrack.boot.resources

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.*
import org.junit.Before
import org.junit.Test

import java.io.IOException
import java.net.URI
import java.time.LocalDateTime

import net.nemerosa.ontrack.json.JsonUtils.array
import net.nemerosa.ontrack.json.JsonUtils.`object`
import net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE
import net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE_OBJECT
import org.junit.Assert.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class CoreResourceModuleTest {

    private lateinit var mapper: ResourceObjectMapper
    private lateinit var securityService: SecurityService
    private lateinit var structureService: StructureService
    private lateinit var projectFavouriteService: ProjectFavouriteService
    private lateinit var branchFavouriteService: BranchFavouriteService

    @Before
    fun before() {
        securityService = mock(SecurityService::class.java)
        structureService = mock(StructureService::class.java)
        val resourceDecorationContributorService = mock(ResourceDecorationContributorService::class.java)
        projectFavouriteService = mock(ProjectFavouriteService::class.java)
        branchFavouriteService = mock(BranchFavouriteService::class.java)
        mapper = ResourceObjectMapperFactory().resourceObjectMapper(
                listOf<ResourceModule>(DefaultResourceModule(
                        listOf(
                                ConnectedAccountResourceDecorator(),
                                ProjectResourceDecorator(resourceDecorationContributorService, projectFavouriteService),
                                BranchResourceDecorator(resourceDecorationContributorService, branchFavouriteService),
                                PromotionLevelResourceDecorator(),
                                ValidationStampResourceDecorator(),
                                BuildResourceDecorator(resourceDecorationContributorService),
                                PromotionRunResourceDecorator(),
                                ValidationRunResourceDecorator(),
                                BuildFilterResourceDecorator(),
                                AccountResourceDecorator(),
                                AccountGroupResourceDecorator(),
                                GlobalPermissionResourceDecorator(),
                                ProjectPermissionResourceDecorator(),
                                JobStatusResourceDecorator(),
                                PredefinedValidationStampResourceDecorator()
                        )
                )),
                DefaultResourceContext(MockURIBuilder(), securityService)
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun project_granted_for_update() {
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE)
        `when`(securityService.isProjectFunctionGranted(1, ProjectEdit::class.java)).thenReturn(true)
        assertResourceJson(
                mapper,
                `object`()
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
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_update", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProject:1,")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_disable", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#disableProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun project_not_favourite() {
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE)
        `when`(securityService.isLogged).thenReturn(true)
        assertResourceJson(
                mapper,
                `object`()
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
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_favourite", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#favouriteProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun project_no_favourite_link_if_not_logged() {
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE)
        assertResourceJson(
                mapper,
                `object`()
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
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun project_favourite() {
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE)
        `when`(securityService.isLogged).thenReturn(true)
        `when`(projectFavouriteService.isProjectFavourite(p)).thenReturn(true)
        assertResourceJson(
                mapper,
                `object`()
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
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_unfavourite", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#unfavouriteProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun project_granted_for_update_and_disabled() {
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1)).withDisabled(true)
                .withSignature(SIGNATURE)
        `when`(securityService.isProjectFunctionGranted(1, ProjectEdit::class.java)).thenReturn(true)
        assertResourceJson(
                mapper,
                `object`()
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
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_update", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProject:1,")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_enable", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#enableProject:1")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun branch_no_grant() {
        // Objects
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE)
        // Serialization
        assertResourceJson(
                mapper,
                `object`()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("project", `object`()
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
                                .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
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
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun build_view() {
        // Objects
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE)
        val signatureTime = LocalDateTime.of(2015, 6, 17, 11, 41)
        val build = Build.of(b, NameDescription.nd("1", "Build 1"), Signature.of(signatureTime, "test")).withId(ID.of(1))
        // Serialization
        assertResourceJson(
                mapper,
                `object`()
                        .with("id", 1)
                        .with("name", "1")
                        .with("description", "Build 1")
                        .with("signature", `object`()
                                .with("time", "2015-06-17T11:41:00Z")
                                .with("user", `object`().with("name", "test").end())
                                .end()
                        )
                        .with("branch", `object`()
                                .with("id", 1)
                                .with("name", "B")
                                .with("description", "Branch")
                                .with("disabled", false)
                                .with("project", `object`()
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
                                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
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
                        .with("_buildLinksFrom", "urn:test:net.nemerosa.ontrack.boot.ui.BuildController#getBuildLinksFrom:1,0,10")
                        .with("_runInfo", "urn:test:net.nemerosa.ontrack.boot.ui.RunInfoController#getRunInfo:build,1")
                        .with("_page", "urn:test:#:entity:BUILD:1")
                        .end(),
                build
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun project_not_granted_for_update() {
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1))
                .withSignature(SIGNATURE)
        assertResourceJson(
                mapper,
                `object`()
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
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
                        .with("_actions", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getActions:PROJECT,1")
                        .with("_decorations", "urn:test:net.nemerosa.ontrack.boot.ui.DecorationsController#getDecorations:PROJECT,1")
                        .with("_events", "urn:test:net.nemerosa.ontrack.boot.ui.EventController#getEvents:PROJECT,1,0,10")
                        .with("_page", "urn:test:#:entity:PROJECT:1")
                        .end(),
                p
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun promotion_level_image_link_and_ignored_branch() {
        // Objects
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE)
        val pl = PromotionLevel.of(b, NameDescription("PL", "Promotion level")).withId(ID.of(1)).withSignature(SIGNATURE)
        // Serialization
        assertResourceJson(
                mapper,
                `object`()
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
                Branch::class.java
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun promotion_level_image_link_and_include_branch() {
        // Objects
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1)).withSignature(SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1)).withSignature(SIGNATURE)
        val pl = PromotionLevel.of(b, NameDescription("PL", "Promotion level"))
                .withId(ID.of(1))
                .withSignature(SIGNATURE)
        // Serialization
        assertResourceJson(
                mapper,
                `object`()
                        .with("id", 1)
                        .with("name", "PL")
                        .with("description", "Promotion level")
                        .with("branch", `object`()
                                .with("id", 1)
                                .with("name", "B")
                                .with("description", "Branch")
                                .with("disabled", false)
                                .with("project", `object`()
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
                                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:PROJECT,1")
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
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun resource_collection_with_filtering() {
        val project = Project.of(NameDescription("PRJ", "Project")).withId(ID.of(1))
        val branches = listOf(
                Branch.of(project, NameDescription("B1", "Branch 1")).withId(ID.of(1)).withSignature(SIGNATURE),
                Branch.of(project, NameDescription("B2", "Branch 2")).withId(ID.of(2)).withSignature(SIGNATURE)
        )
        val resourceCollection = Resources.of(
                branches,
                URI.create("urn:branch")
        )

        assertResourceJson(
                mapper,
                `object`()
                        .with("_self", "urn:branch")
                        .with("resources", array()
                                .with(`object`()
                                        .with("id", 1)
                                        .with("name", "B1")
                                        .with("description", "Branch 1")
                                        .with("disabled", false)
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
                                .with(`object`()
                                        .with("id", 2)
                                        .with("name", "B2")
                                        .with("description", "Branch 2")
                                        .with("disabled", false)
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
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun account_group_links() {
        assertResourceJson(
                mapper,
                `object`()
                        .with("id", 0)
                        .with("name", "Admins")
                        .with("description", "Administrators")
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.AccountController#getGroup:0")
                        .with("_update", "urn:test:net.nemerosa.ontrack.boot.ui.AccountController#getGroupUpdateForm:0")
                        .with("_delete", "urn:test:net.nemerosa.ontrack.boot.ui.AccountController#deleteGroup:0")
                        .end(),
                AccountGroup.of("Admins", "Administrators")
        )
    }

    @Test
    @Throws(IOException::class)
    fun promotion_run_delete_granted() {
        // Objects
        val p = Project.of(NameDescription("P", "Project")).withId(ID.of(1))
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1))
        val pl = PromotionLevel.of(b, NameDescription.nd("PL", "Promotion Level")).withId(ID.of(1))
        val build = Build.of(b, NameDescription.nd("1", "Build 1"), Signature.of("test")).withId(ID.of(1))
        val run = PromotionRun.of(build, pl, Signature.of("test"), "Run").withId(ID.of(1))
        // Security
        `when`(securityService.isProjectFunctionGranted(1, PromotionRunDelete::class.java)).thenReturn(true)
        // Serialization
        val node = mapper.objectMapper.readTree(mapper.write(run))
        // Checks the _delete link is present
        val delete = JsonUtils.get(node, "_delete")
        assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.PromotionRunController#deletePromotionRun:1", delete)
    }

    companion object {

        @Throws(JsonProcessingException::class)
        fun assertResourceJson(mapper: ResourceObjectMapper, expectedJson: JsonNode, o: Any) {
            assertEquals(
                    mapper.objectMapper.writeValueAsString(expectedJson),
                    mapper.write(o)
            )
        }

        @Throws(JsonProcessingException::class)
        fun assertResourceJson(mapper: ResourceObjectMapper, expectedJson: JsonNode, o: Any, view: Class<*>) {
            assertEquals(
                    mapper.objectMapper.writeValueAsString(expectedJson),
                    mapper.write(o, view)
            )
        }
    }

}
