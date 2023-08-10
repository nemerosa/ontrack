package net.nemerosa.ontrack.extension.stash.scm

import net.nemerosa.ontrack.common.BaseException

class BitbucketServerSCMMissingAutoMergeTokenException(configName: String): BaseException(
    """Auto approval/merge was requested but no auto merge token was provided in the "$configName" Bitbucket Server configuration."""
)