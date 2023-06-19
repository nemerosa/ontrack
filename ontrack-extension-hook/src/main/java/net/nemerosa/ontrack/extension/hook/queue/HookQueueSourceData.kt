package net.nemerosa.ontrack.extension.hook.queue

data class HookQueueSourceData(
        /**
         * Hook processor ID
         */
        val hook: String,
        /**
         * ID of the hook record
         */
        val id: String,
)