package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.common.BaseException
import java.time.Duration

class GitRepoRemoteTimeoutException(
    message: String,
    retries: UInt,
    interval: Duration,
) : BaseException(
    "Maximum number of retries reached for $message ($retries retries every $interval, still getting connection errors)."
)