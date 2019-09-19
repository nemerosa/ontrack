package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class LabelIdNotFoundException(id: Int) : NotFoundException("Label with ID $id cannot be found")
