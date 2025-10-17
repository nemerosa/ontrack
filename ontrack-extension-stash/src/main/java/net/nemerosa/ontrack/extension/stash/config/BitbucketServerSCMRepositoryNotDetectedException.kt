package net.nemerosa.ontrack.extension.stash.config

import net.nemerosa.ontrack.model.exceptions.InputException

class BitbucketServerSCMRepositoryNotDetectedException(scmUrl: String) : InputException(
    "Could not find any Bitbucket Server configuration for SCM URL: $scmUrl"
)