package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class AutoVersioningAuditStoreMigrationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditStore: AutoVersioningAuditStore

    @Autowired
    private lateinit var autoVersioningAuditStoreMigration: AutoVersioningAuditStoreMigration

    @Autowired
    private lateinit var entityDataStore: EntityDataStore

    @Test
    @AsAdminTest
    fun `Migration of auto-versioning audit entries`() {
        // Creation of old style audit entries
        project {
            branch {
                entityDataStore.addObject(
                    entity = this,
                    category = AutoVersioningAuditStoreMigration.CATEGORY,
                    name = uuid,
                    signature = Signature.of("test"),
                    groupName = null,
                    data = json.parseAsJson(),
                )
                // Running the migration
                autoVersioningAuditStoreMigration.start()
                // Checks that the record is now in the table
                val entry = autoVersioningAuditStore.findByUUID(this, uuid)
                    ?: fail("Could not find migrated entry")
                // Checks the data
                entry.apply {
                    assertEquals(
                        AutoVersioningAuditState.THROTTLED,
                        mostRecentState.state,
                    )
                }
                // Checks that the record is gone
                assertTrue(
                    entityDataStore.getByFilter(
                        EntityDataStoreFilter(
                            entity = this,
                            category = AutoVersioningAuditStoreMigration.CATEGORY,
                            name = uuid,
                        )
                    ).isEmpty(),
                    "Old record is gone"
                )
            }
        }
    }

    companion object {
        private val uuid = UUID.randomUUID().toString()
        private val json = """
            {"queue": null, "states": [{"data": {}, "state": "PROCESSING_CANCELLED", "signature": {"time": "2025-11-19T18:40:37.474048581Z", "user": {"name": "jenkins"}}}, {"data": {}, "state": "CREATED", "signature": {"time": "2025-11-19T18:40:37.373583181Z", "user": {"name": "jenkins"}}}], "routing": "", "running": true, "qualifier": null, "reviewers": [], "targetPaths": ["beta/ontrack-demo-beta/config.yaml"], "targetRegex": null, "autoApproval": true, "sourceBuildId": 10635, "sourceProject": "ontrack", "targetVersion": "5.0-beta.0", "upgradeBranch": null, "postProcessing": null, "prBodyTemplate": null, "targetProperty": "#root[0].version", "additionalPaths": [], "mostRecentState": "PROCESSING_START", "prTitleTemplate": null, "sourcePromotion": null, "validationStamp": null, "autoApprovalMode": "CLIENT", "targetPropertyType": "yaml", "targetPropertyRegex": null, "postProcessingConfig": null, "prBodyTemplateFormat": null, "sourceBackValidation": null, "sourcePromotionRunId": null, "upgradeBranchPattern": "feature/auto-upgrade-<project>-<version>-<branch>"}
        """.trimIndent()
    }

}