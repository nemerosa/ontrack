package net.nemerosa.ontrack.extension.stash.scm

import net.nemerosa.ontrack.common.BaseException

class BitbucketServerSCMMissingAutoMergeUserException(configName: String): BaseException(
    """Auto approval/merge was requested but no auto merge user was provided in the "$configName" Bitbucket Server configuration."""
)