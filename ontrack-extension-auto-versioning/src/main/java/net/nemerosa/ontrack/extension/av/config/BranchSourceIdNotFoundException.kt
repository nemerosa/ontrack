package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class BranchSourceIdNotFoundException(id: String) : NotFoundException("Branch source with ID $id cannot be found.")
