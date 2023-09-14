package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class LabelNotFoundException(category: String?, name: String) : NotFoundException(
    "Can not find label with display: $category:$name"
)