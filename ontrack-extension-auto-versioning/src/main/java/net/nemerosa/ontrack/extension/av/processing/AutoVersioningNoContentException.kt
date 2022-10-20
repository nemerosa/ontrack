package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.common.BaseException

class AutoVersioningNoContentException(
    branch: String,
    path: String
): BaseException(
    """Path at $path for branch $branch was not found or has no content."""
)