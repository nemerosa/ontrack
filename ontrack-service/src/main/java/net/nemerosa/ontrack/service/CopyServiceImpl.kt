package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.Document.Companion.isValid
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.security.BranchEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CopyServiceImpl(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val securityService: SecurityService,
    private val buildFilterService: BuildFilterService
) : CopyService {

    override fun copy(targetBranch: Branch, request: BranchCopyRequest): Branch {
        // Replacement function
        val replacementFn = Replacement.replacementFn(request.replacements)
        // Gets the source branch
        val sourceBranch = structureService.getBranch(request.sourceBranchId)
        // Actual copy
        return copy(targetBranch, sourceBranch, replacementFn)
    }

    override fun copy(
        targetBranch: Branch,
        sourceBranch: Branch,
        replacementFn: (String) -> String,
    ): Branch {
        // If the same branch, rejects
        if (sourceBranch.id() == targetBranch.id()) {
            throw CannotCopyItselfException()
        }
        // Checks the rights on the target branch
        securityService.checkProjectFunction(targetBranch, BranchEdit::class.java)
        // Now, we can work in a secure context
        securityService.asAdmin<Boolean?> { doCopy(sourceBranch, targetBranch, replacementFn) }
        // OK
        return targetBranch
    }

    override fun cloneBranch(branch: Branch, request: BranchCloneRequest): Branch {
        // Replacement function
        val replacementFn = Replacement.replacementFn(request.replacements)
        // Description of the target branch
        val targetDescription: String? = replacementFn(branch.description ?: "")
        // Creates the branch
        val targetBranch = structureService.newBranch(
            of(
                branch.project,
                nd(request.name, targetDescription)
            )
        )
        // Copies the configuration
        doCopy(branch, targetBranch, replacementFn)
        // OK
        return targetBranch
    }

    override fun cloneProject(project: Project, request: ProjectCloneRequest): Project {
        // Replacement function
        val replacementFn = Replacement.replacementFn(request.replacements)

        // Description of the target project
        val targetProjectDescription: String? = replacementFn(project.description ?: "")

        // Creates the project
        val targetProject = structureService.newProject(
            of(
                nd(request.name, targetProjectDescription)
            )
        )

        // Copies the properties for the project
        doCopyProperties(project, targetProject, replacementFn)

        // Creates a copy of the branch
        val sourceBranch = structureService.getBranch(request.sourceBranchId)
        val targetBranchName = replacementFn(sourceBranch.name)
        val targetBranchDescription: String? = replacementFn(sourceBranch.description ?: "")
        val targetBranch = structureService.newBranch(
            of(
                targetProject,
                nd(targetBranchName, targetBranchDescription)
            )
        )

        // Configuration of the new branch
        doCopy(sourceBranch, targetBranch, replacementFn)

        // OK
        return targetProject
    }

    override fun update(branch: Branch, request: BranchBulkUpdateRequest): Branch {
        // Replacement function
        val replacementFn = Replacement.replacementFn(request.replacements)
        // Description update
        val updatedBranch = branch.withDescription(
            replacementFn(branch.description ?: "")
        )
        structureService.saveBranch(updatedBranch)
        // Updating
        doCopy(branch, updatedBranch, replacementFn)
        // Reloads the branch
        return structureService.getBranch(branch.id)
    }

    private fun doCopy(
        sourceBranch: Branch,
        targetBranch: Branch,
        replacementFn: (String) -> String,
    ): Boolean {
        // Branch properties
        doCopyProperties(sourceBranch, targetBranch, replacementFn)
        // Validation stamps and properties
        doCopyValidationStamps(sourceBranch, targetBranch, replacementFn)
        // Promotion level and properties
        doCopyPromotionLevels(sourceBranch, targetBranch, replacementFn)
        // User filters
        doCopyUserBuildFilters(sourceBranch, targetBranch)
        // OK
        return true
    }

    private fun doCopyUserBuildFilters(sourceBranch: Branch, targetBranch: Branch) {
        buildFilterService.copyToBranch(sourceBranch.id, targetBranch.id)
    }

    private fun doCopyPromotionLevels(
        sourceBranch: Branch,
        targetBranch: Branch,
        replacementFn: (String) -> String,
    ) {

        fun copyPromotionLevelContent(
            sourcePromotionLevel: PromotionLevel,
            targetPromotionLevel: PromotionLevel
        ) {
            // Copy of the image
            val image = structureService.getPromotionLevelImage(sourcePromotionLevel.id)
            if (isValid(image)) {
                structureService.setPromotionLevelImage(targetPromotionLevel.id, image)
            }
            // Copy of properties
            doCopyProperties(sourcePromotionLevel, targetPromotionLevel, replacementFn)
        }

        syncForward(
            from = structureService.getPromotionLevelListForBranch(sourceBranch.id),
            to = structureService.getPromotionLevelListForBranch(targetBranch.id),
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { sourcePromotionLevel ->
                val targetPromotionLevel = structureService.newPromotionLevel(
                    PromotionLevel.of(
                        targetBranch,
                        nd(
                            sourcePromotionLevel.name,
                            replacementFn(sourcePromotionLevel.description ?: "")
                        )
                    )
                )
                copyPromotionLevelContent(sourcePromotionLevel, targetPromotionLevel)
            }
            onModification { sourcePromotionLevel, targetPromotionLevel ->
                structureService.savePromotionLevel(
                    targetPromotionLevel.withDescription(
                        replacementFn(sourcePromotionLevel.description ?: "")
                    )
                )
                copyPromotionLevelContent(sourcePromotionLevel, targetPromotionLevel)
            }
            onDeletion { target ->
                structureService.deletePromotionLevel(target.id)
            }
        }
    }

    private fun doCopyProperties(
        source: ProjectEntity,
        target: ProjectEntity,
        replacementFn: (String) -> String,
    ) {
        syncForward(
            from = propertyService.getProperties(source),
            to = propertyService.getProperties(target)
        ) {
            equality { a, b -> a.type.name == b.type.name }
            onCreation { sourceProperty ->
                doCopyProperty(source, sourceProperty, target, replacementFn)
            }
            onModification { sourceProperty, targetProperty ->
                doCopyProperty(source, sourceProperty, target, replacementFn)
            }
            onDeletion { targetProperty ->
                propertyService.deleteProperty(target, targetProperty.type.typeName)
            }
        }
    }

    fun doCopyValidationStamps(
        sourceBranch: Branch,
        targetBranch: Branch,
        replacementFn: (String) -> String,
    ) {

        fun copyValidationStampContent(
            sourceValidationStamp: ValidationStamp,
            targetValidationStamp: ValidationStamp
        ) {
            // Copy of the image
            val image = structureService.getValidationStampImage(sourceValidationStamp.id)
            if (isValid(image)) {
                structureService.setValidationStampImage(targetValidationStamp.id, image)
            }
            // Copy of properties
            doCopyProperties(sourceValidationStamp, targetValidationStamp, replacementFn)
        }

        syncForward(
            from = structureService.getValidationStampListForBranch(sourceBranch.id),
            to = structureService.getValidationStampListForBranch(targetBranch.id),
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { sourceValidationStamp ->
                val targetValidationStamp = structureService.newValidationStamp(
                    ValidationStamp.of(
                        targetBranch,
                        nd(
                            sourceValidationStamp.name,
                            replacementFn(sourceValidationStamp.description ?: "")
                        )
                    )
                )
                copyValidationStampContent(sourceValidationStamp, targetValidationStamp)
            }
            onModification { sourceValidationStamp, targetValidationStamp ->
                structureService.saveValidationStamp(
                    targetValidationStamp.withDescription(
                        replacementFn(sourceValidationStamp.description ?: "")
                    )
                )
                copyValidationStampContent(sourceValidationStamp, targetValidationStamp)
            }
            onDeletion { targetValidationStamp ->
                structureService.deleteValidationStamp(targetValidationStamp.id)
            }
        }
    }

    private fun <T> doCopyProperty(
        sourceEntity: ProjectEntity,
        property: Property<T>,
        targetEntity: ProjectEntity,
        replacementFn: (String) -> String,
    ) {
        if (!property.isEmpty && property.getType().canEdit(targetEntity, securityService)) {
            // Copy of the property
            propertyService.copyProperty(
                sourceEntity,
                property,
                targetEntity,
                replacementFn
            )
        }
    }
}
