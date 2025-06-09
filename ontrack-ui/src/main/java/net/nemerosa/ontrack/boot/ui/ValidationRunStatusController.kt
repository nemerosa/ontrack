package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusIDNotAvailableException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/validationRunStatuses")
class ValidationRunStatusController(
        private val structureService: StructureService
) {

    /**
     * Edition of a validation run status comment
     */

    @PutMapping("{validationRunStatusId}/comment")
    fun validationRunStatusEditComment(
            @PathVariable validationRunStatusId: ID,
            @RequestBody request: ValidationRunStatusCommentRequest
    ): ValidationRun {
        // Gets the parent run
        val run = structureService.getParentValidationRun(validationRunStatusId)
        // Not found
                ?: throw ValidationRunStatusIDNotAvailableException(validationRunStatusId)
        // Edits the comment
        return structureService.saveValidationRunStatusComment(
                run,
                validationRunStatusId,
                request.comment
        )
    }
}