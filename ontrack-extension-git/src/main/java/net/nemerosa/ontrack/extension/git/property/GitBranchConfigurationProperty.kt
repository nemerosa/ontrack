package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.model.structure.ServiceConfiguration

class GitBranchConfigurationProperty(

        /**
         * Git branch or pull request ID
         */
        val branch: String,

        /**
         * Build link
         */
        val buildCommitLink: ServiceConfiguration?,

        /**
         * Build overriding policy when synchronizing
         */
        val isOverride: Boolean,

        /**
         * Interval in minutes for build/tag synchronization
         */
        val buildTagInterval: Int

)
