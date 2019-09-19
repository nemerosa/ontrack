package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Memo
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/validationRunStatuses")
class ValidationRunStatusController(
        private val structureService: StructureService
) : AbstractResourceController() {

    /**
     * Edition of a validation run status comment
     */

    @GetMapping("{validationRunStatusId}/comment")
    fun getValidationRunStatusEditCommentForm(
            @PathVariable validationRunStatusId: ID
    ): Form {
        val status = structureService.getValidationRunStatus(validationRunStatusId)
        return Form.create()
                .with(
                        Memo.of("comment")
                                .label("Comment")
                                .help("Comment for this validation run status")
                                .value(status.description)
                )
    }

    @PutMapping("{validationRunStatusId}/comment")
    fun validationRunStatusEditComment(
            @PathVariable validationRunStatusId: ID,
            @RequestBody request: ValidationRunStatusCommentRequest
    ): ValidationRun {
        // Gets the parent run
        val run = structureService.getParentValidationRun(validationRunStatusId)
        // Edits the comment
        return structureService.saveValidationRunStatusComment(
                run,
                validationRunStatusId,
                request.comment
        )
    }
}