package net.nemerosa.ontrack.extension.stash.config

import net.nemerosa.ontrack.model.exceptions.InputException

class BitbucketServerSCMUnexistingConfigException : InputException(
    "Could not find any matching Bitbucket Server SCM configuration."
)