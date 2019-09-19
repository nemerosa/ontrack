package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.exceptions.InputException

class LabelCategoryNameAlreadyExistException(category: String?, name: String) : InputException(
        "Label with category ${category ?: "n/a"} and name $name already exists."
) {
}