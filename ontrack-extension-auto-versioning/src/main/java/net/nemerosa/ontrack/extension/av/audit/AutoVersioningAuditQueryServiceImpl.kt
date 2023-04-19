package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.recordings.RecordingsQueryService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningAuditQueryServiceImpl(
        private val autoVersioningRecordingsExtension: AutoVersioningRecordingsExtension,
        private val recordingsQueryService: RecordingsQueryService,
        private val structureService: StructureService,
) : AutoVersioningAuditQueryService {

    override fun findByUUID(branch: Branch, uuid: String): AutoVersioningAuditEntry? =
            recordingsQueryService.findById(autoVersioningRecordingsExtension, uuid)
                    ?.toEntry()

    override fun findByFilter(filter: AutoVersioningAuditQueryFilter, offset: Int, size: Int): List<AutoVersioningAuditEntry> =
            recordingsQueryService.findByFilter(autoVersioningRecordingsExtension, filter, offset, size)
                    .map { it.toEntry() }
                    .pageItems

    private fun AutoVersioningAuditStoreData.toEntry() = AutoVersioningAuditEntry(
            order = AutoVersioningOrder(
                    uuid = uuid,
                    branch = structureService.getBranch(ID.of(branchId)),
                    sourceProject = sourceProject,
                    targetPaths = targetPaths,
                    targetRegex = targetRegex,
                    targetProperty = targetProperty,
                    targetPropertyRegex = targetPropertyRegex,
                    targetPropertyType = targetPropertyType,
                    targetVersion = targetVersion,
                    autoApproval = autoApproval,
                    upgradeBranchPattern = upgradeBranchPattern,
                    postProcessing = postProcessing,
                    postProcessingConfig = postProcessingConfig,
                    validationStamp = validationStamp,
                    autoApprovalMode = autoApprovalMode,
            ),
            audit = states,
            routing = routing,
            queue = queue,
    )

}