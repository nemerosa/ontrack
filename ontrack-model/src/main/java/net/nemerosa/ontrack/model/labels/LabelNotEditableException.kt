package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.exceptions.InputException

class LabelNotEditableException(label: Label) : InputException(
        "Label ${label.category}:${label.name} is not editable because it is computed."
)
