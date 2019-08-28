package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.exceptions.InputException

class ReleaseNotesGenerationExtensionMissingException(
        projectName: String
) : InputException("No release notes generation can be found for project $projectName.")