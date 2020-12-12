package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.boot.ui.AbstractWebTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceModule
import net.nemerosa.ontrack.ui.resource.ResourceDecorator
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpOutputMessage
import java.io.ByteArrayOutputStream
import java.io.IOException

class ResourceHttpMessageConverterIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var decorators: Collection<ResourceDecorator<*>>

    private lateinit var converter: ResourceHttpMessageConverter

    @Before
    fun before() {
        converter = ResourceHttpMessageConverter(
                MockURIBuilder(), securityService, listOf(DefaultResourceModule(decorators)))
    }

    @Test
    @Throws(IOException::class)
    fun branch() {
        // Objects
        val p = Project.of(NameDescription("P", "Projet créé")).withId(ID.of(1))
                .withSignature(TestFixtures.SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1))
                .withSignature(TestFixtures.SIGNATURE)
        // Message
        val message = Mockito.mock(HttpOutputMessage::class.java)
        val output = ByteArrayOutputStream()
        Mockito.`when`(message.body).thenReturn(output)
        // Serialization
        converter.writeInternal(b, message)
        // Content
        val json = output.toByteArray().toString(Charsets.UTF_8)
        // Parsing
        val node = ObjectMapperFactory.create().readTree(json)
        // Check
        TestUtils.assertJsonEquals(
                JsonUtils.`object`()
                        .with("id", 1)
                        .with("name", "B")
                        .with("description", "Branch")
                        .with("disabled", false)
                        .with("project", JsonUtils.`object`()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Projet créé")
                                .with("disabled", false)
                                .with("signature", TestFixtures.SIGNATURE_OBJECT)
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
                        .with("signature", TestFixtures.SIGNATURE_OBJECT)
                        .with("_self", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranch:1")
                        .with("_project", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#getProject:1")
                        .with("_promotionLevels", "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelListForBranch:1")
                        .with("_validationStamps", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampListForBranch:1")
                        .with("_validationStampViews", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampViewListForBranch:1")
                        .with("_allValidationStampFilters", "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampFilterController#getAllBranchValidationStampFilters:1")
                        .with("_branches", "urn:test:net.nemerosa.ontrack.boot.ui.BranchController#getBranchListForProject:1")
                        .with("_properties", "urn:test:net.nemerosa.ontrack.boot.ui.PropertyController#getProperties:BRANCH,1")
                        .with("_extra", "urn:test:net.nemerosa.ontrack.boot.ui.ProjectEntityExtensionController#getInformation:BRANCH,1")
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
        )
    }

    @Test
    @Throws(Exception::class)
    fun branch_disable_granted_for_automation() { // Objects
        val p = Project.of(NameDescription("P", "Projet créé")).withId(ID.of(1))
                .withSignature(TestFixtures.SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1))
                .withSignature(TestFixtures.SIGNATURE)
        // Message
        val message = Mockito.mock(HttpOutputMessage::class.java)
        val output = ByteArrayOutputStream()
        Mockito.`when`(message.body).thenReturn(output)
        // Serialization
        asGlobalRole("AUTOMATION").execute { converter.writeInternal(b, message) }
        // Content
        val json = output.toByteArray().toString(Charsets.UTF_8)
        // Parsing
        val node = ObjectMapperFactory.create().readTree(json)
        // Disable link
        Assert.assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.BranchController#disableBranch:1", node.path("_disable").asText())
    }

    @Test
    @Throws(Exception::class)
    fun branch_enable_granted_for_automation() { // Objects
        val p = Project.of(NameDescription("P", "Projet créé")).withId(ID.of(1))
                .withSignature(TestFixtures.SIGNATURE)
        val b = Branch.of(p, NameDescription("B", "Branch")).withId(ID.of(1))
                .withDisabled(true)
                .withSignature(TestFixtures.SIGNATURE)
        // Message
        val message = Mockito.mock(HttpOutputMessage::class.java)
        val output = ByteArrayOutputStream()
        Mockito.`when`(message.body).thenReturn(output)
        // Serialization
        asGlobalRole("AUTOMATION").execute { converter.writeInternal(b, message) }
        // Content
        val json = output.toByteArray().toString(Charsets.UTF_8)
        // Parsing
        val node = ObjectMapperFactory.create().readTree(json)
        // Enable link
        Assert.assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.BranchController#enableBranch:1", node.path("_enable").asText())
    }
}