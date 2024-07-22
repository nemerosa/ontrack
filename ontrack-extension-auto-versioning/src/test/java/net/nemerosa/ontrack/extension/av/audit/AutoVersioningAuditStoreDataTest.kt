package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningAuditStoreDataTest {

    @Test
    fun `Reading JSON record with the running flag`() {
        val json = mapOf(
            "sourceProject" to "test",
            "targetPaths" to listOf(
                "gradle.properties"
            ),
            "targetRegex" to null,
            "targetProperty" to "version",
            "targetPropertyRegex" to null,
            "targetPropertyType" to null,
            "targetVersion" to "2.0.0",
            "autoApproval" to true,
            "upgradeBranchPattern" to "feature/version-<version>",
            "postProcessing" to null,
            "postProcessingConfig" to null,
            "validationStamp" to null,
            "running" to true,
            "autoApprovalMode" to "CLIENT",
            "states" to listOf(
                mapOf(
                    "state" to "CREATED",
                    "data" to emptyMap<String, String>(),
                    "signature" to mapOf(
                        "time" to Time.store(Time.now()),
                        "user" to mapOf(
                            "name" to "test"
                        )
                    )
                )
            ),
            "routing" to "routing",
            "queue" to null,
        ).asJson()
        val data: AutoVersioningAuditStoreData = json.parse()
        assertEquals(AutoVersioningAuditState.CREATED, data.mostRecentState)
        assertEquals(true, data.running)
        assertEquals(AutoApprovalMode.CLIENT, data.autoApprovalMode)
    }

    @Test
    fun `Most recent state in JSON`() {
        val data = AutoVersioningAuditStoreData(
            sourceProject = "test",
            sourceBuildId = null,
            sourcePromotionRunId = null,
            sourcePromotion = null,
            sourceBackValidation = null,
            targetPaths = listOf("gradle.properties"),
            targetRegex = null,
            targetProperty = "version",
            targetPropertyRegex = null,
            targetPropertyType = null,
            targetVersion = "2.0.0",
            autoApproval = true,
            upgradeBranchPattern = "feature/version-<version>",
            upgradeBranch = null,
            postProcessing = null,
            postProcessingConfig = NullNode.instance,
            validationStamp = null,
            autoApprovalMode = AutoApprovalMode.SCM,
            states = listOf(
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.RECEIVED, emptyMap()),
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.CREATED, emptyMap())
            ),
            routing = "routing",
            queue = "queue",
            reviewers = null,
            prTitleTemplate = null,
            prBodyTemplate = null,
            prBodyTemplateFormat = null,
            trailId = null,
        )
        assertEquals(AutoVersioningAuditState.RECEIVED, data.mostRecentState)
        val json = data.asJson()
        assertEquals(
            "RECEIVED",
            json.path("mostRecentState").asText()
        )
    }

    @Test
    fun `Running flag in JSON`() {
        val data = AutoVersioningAuditStoreData(
            sourceProject = "test",
            sourceBuildId = null,
            sourcePromotionRunId = null,
            sourcePromotion = null,
            sourceBackValidation = null,
            targetPaths = listOf("gradle.properties"),
            targetRegex = null,
            targetProperty = "version",
            targetPropertyRegex = null,
            targetPropertyType = null,
            targetVersion = "2.0.0",
            autoApproval = true,
            upgradeBranchPattern = "feature/version-<version>",
            upgradeBranch = null,
            postProcessing = null,
            postProcessingConfig = NullNode.instance,
            validationStamp = null,
            autoApprovalMode = AutoApprovalMode.SCM,
            states = listOf(
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.RECEIVED, emptyMap()),
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.CREATED, emptyMap())
            ),
            routing = "routing",
            queue = "queue",
            reviewers = null,
            prTitleTemplate = null,
            prBodyTemplate = null,
            prBodyTemplateFormat = null,
            trailId = null,
        )
        assertEquals(AutoVersioningAuditState.RECEIVED, data.mostRecentState)
        val json = data.asJson()
        assertEquals(
            true,
            json.path("running").asBoolean()
        )
    }

    @Test
    fun `Running flag in JSON for an error final state`() {
        val data = AutoVersioningAuditStoreData(
            sourceProject = "test",
            sourceBuildId = null,
            sourcePromotionRunId = null,
            sourcePromotion = null,
            sourceBackValidation = null,
            targetPaths = listOf("gradle.properties"),
            targetRegex = null,
            targetProperty = "version",
            targetPropertyRegex = null,
            targetPropertyType = null,
            targetVersion = "2.0.0",
            autoApproval = true,
            upgradeBranchPattern = "feature/version-<version>",
            upgradeBranch = null,
            postProcessing = null,
            postProcessingConfig = NullNode.instance,
            validationStamp = null,
            autoApprovalMode = AutoApprovalMode.SCM,
            states = listOf(
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.ERROR, emptyMap()),
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.RECEIVED, emptyMap()),
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.CREATED, emptyMap())
            ),
            routing = "routing",
            queue = "queue",
            reviewers = null,
            prTitleTemplate = null,
            prBodyTemplate = null,
            prBodyTemplateFormat = null,
            trailId = null,
        )
        assertEquals(AutoVersioningAuditState.ERROR, data.mostRecentState)
        val json = data.asJson()
        assertEquals(
            false,
            json.path("running").asBoolean()
        )
    }

    @Test
    fun `Most recent state from JSON`() {
        val data = AutoVersioningAuditStoreData(
            sourceProject = "test",
            sourceBuildId = null,
            sourcePromotionRunId = null,
            sourcePromotion = null,
            sourceBackValidation = null,
            targetPaths = listOf("gradle.properties"),
            targetRegex = null,
            targetProperty = "version",
            targetPropertyRegex = null,
            targetPropertyType = null,
            targetVersion = "2.0.0",
            autoApproval = true,
            upgradeBranchPattern = "feature/version-<version>",
            upgradeBranch = null,
            postProcessing = null,
            postProcessingConfig = NullNode.instance,
            validationStamp = null,
            autoApprovalMode = AutoApprovalMode.SCM,
            states = listOf(
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.RECEIVED, emptyMap()),
                AutoVersioningAuditEntryState(Signature.of("test"), AutoVersioningAuditState.CREATED, emptyMap())
            ),
            routing = "routing",
            queue = "queue",
            reviewers = null,
            prTitleTemplate = null,
            prBodyTemplate = null,
            prBodyTemplateFormat = null,
            trailId = null,
        )
        assertEquals(AutoVersioningAuditState.RECEIVED, data.mostRecentState)
        val json = data.asJson()
        // Parsing
        val parsed = json.parse<AutoVersioningAuditStoreData>()
        assertEquals(AutoVersioningAuditState.RECEIVED, parsed.mostRecentState)
    }

}