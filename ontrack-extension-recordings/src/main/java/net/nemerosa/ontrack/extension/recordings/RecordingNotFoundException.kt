package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class RecordingNotFoundException(store: String, id: String) : NotFoundException(
        """Record $id in store $store cannot be found."""
)