package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.exceptions.InputException

class ReleaseNotesServiceBranchOrderingNotFoundException(id: String) : InputException(
        """Branch ordering with ID "$id" cannot be found."""
)