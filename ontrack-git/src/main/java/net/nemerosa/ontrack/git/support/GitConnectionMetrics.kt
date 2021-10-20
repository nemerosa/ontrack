package net.nemerosa.ontrack.git.support

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

/**
 * Metrics about the connection issues with Git.
 */
@Component
class GitConnectionMetrics : MeterBinder {

    override fun bindTo(registry: MeterRegistry) {
        registry.gauge(
            "ontrack_git_connect_errors",
            this
        ) {
            connectErrors.get().toDouble()
        }
        registry.gauge(
            "ontrack_git_connect_retries",
            this
        ) {
            connectRetries.get().toDouble()
        }
    }

    companion object {

        /**
         * Number of terminal connection errors
         */
        private val connectErrors = AtomicInteger()

        /**
         * Number of retries on connection errors
         */
        private val connectRetries = AtomicInteger()

        /**
         * Feeding a genuine connection error
         */
        fun connectError() {
            connectErrors.incrementAndGet()
        }

        /**
         * Incrementing the number of retries on connection error
         */
        fun connectRetry() {
            connectRetries.incrementAndGet()
        }

    }

}