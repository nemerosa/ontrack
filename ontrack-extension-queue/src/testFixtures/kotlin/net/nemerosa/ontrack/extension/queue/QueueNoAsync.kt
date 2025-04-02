package net.nemerosa.ontrack.extension.queue

import org.springframework.test.context.TestPropertySource

/**
 * Annotates a test to specify that the asynchronous processing of queues will be disabled.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@TestPropertySource(
    properties = [
        "ontrack.extension.queue.general.async=false",
    ]
)
annotation class QueueNoAsync
