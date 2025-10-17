package net.nemerosa.ontrack.extension.stash.config

import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.config.model.EnvConstants
import net.nemerosa.ontrack.extension.stash.BitbucketServerFixtures

object BitbucketServerSCMEnvFixtures {
    fun bitbucketServerEnv() =
        EnvFixtures.generic() + mapOf(
            EnvConstants.GENERIC_SCM_URL to "${BitbucketServerFixtures.BITBUCKET_SERVER_URL}/scm/nemerosa/yontrack.git",
        )
}
