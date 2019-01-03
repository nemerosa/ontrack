package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.form.Color
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Memo
import net.nemerosa.ontrack.model.form.Text
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/labels")
class LabelController {

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

}