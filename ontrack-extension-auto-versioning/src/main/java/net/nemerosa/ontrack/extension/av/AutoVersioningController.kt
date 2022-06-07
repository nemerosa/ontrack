package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.av.queue.AutoVersioningQueueStats
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@RestController
@RequestMapping("/extension/auto-versioning")
class AutoVersioningController(
    private val queueStats: AutoVersioningQueueStats,
) : AbstractResourceController() {
    /**
     * Gets some statistics about the processing of PR creation orders.
     */
    @GetMapping("stats")
    fun getStats(): Resource<AutoVersioningStats> =
        Resource.of(
            AutoVersioningStats(
                pendingOrders = queueStats.pendingOrders,
            ),
            uri(MvcUriComponentsBuilder.on(AutoVersioningController::class.java).getStats())
        )

}