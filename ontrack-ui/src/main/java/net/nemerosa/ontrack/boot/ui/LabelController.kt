package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Color
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Memo
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.LabelNotEditableException
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/rest/labels")
class LabelController(
        private val labelManagementService: LabelManagementService
) : AbstractResourceController() {

    /**
     * Creation form for a label
     */
    @GetMapping("create")
    fun getCreationForm(): Form = Form.create()
            .with(
                    Text.of("category")
                            .label("Category")
                            .help("Category the label belongs to")
                            .length(50)
                            .optional()
            )
            .with(
                    Text.of("name")
                            .label("Name")
                            .help("Name of the label")
                            .length(200)
            )
            .with(
                    Memo.of("description")
                            .label("Description")
                            .help("Short description of the label")
                            .length(500)
                            .optional()
            )
            .with(
                    Color.of("color")
                            .label("Color")
                            .help("Color of the label")
                            .value("#000000")
            )

    /**
     * Creation of a label
     */
    @PostMapping("create")
    fun newLabel(@RequestBody @Valid form: LabelForm): Label =
            labelManagementService.newLabel(form)

    /**
     * Gets the form to update a label.
     */
    @GetMapping("{labelId}/update")
    fun getUpdateLabelForm(@PathVariable labelId: Int): Form =
            labelManagementService
                    .getLabel(labelId)
                    .let { label ->
                        if (label.computedBy == null) {
                            getCreationForm()
                                    .fill("category", label.category)
                                    .fill("name", label.name)
                                    .fill("description", label.description)
                                    .fill("color", label.color)
                        } else {
                            throw LabelNotEditableException(label)
                        }
                    }

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