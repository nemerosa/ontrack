package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.common.BaseException
import java.io.IOException

class OntrackGitHubClientException(e: IOException?) : BaseException(e, "Error while accessing GitHub: %s", e)