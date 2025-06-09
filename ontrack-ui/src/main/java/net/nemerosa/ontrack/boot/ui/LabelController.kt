package net.nemerosa.ontrack.boot.ui

import jakarta.validation.Valid
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/labels")
class LabelController(
    private val labelManagementService: LabelManagementService
) : AbstractResourceController() {

    /**
     * Creation of a label
     */
    @PostMapping("create")
    fun newLabel(@RequestBody @Valid form: LabelForm): Label =
        labelManagementService.newLabel(form)

    /**
     * Updates a label
     */
    @PutMapping("{labelId}/update")
    fun updateLabel(@PathVariable labelId: Int, @RequestBody @Valid form: LabelForm): Label =
        labelManagementService.updateLabel(labelId, form)

    /**
     * Deleting a label
     */
    @DeleteMapping("{labelId}/delete")
    fun deleteLabel(@PathVariable labelId: Int): Ack =
        labelManagementService.deleteLabel(labelId)

}