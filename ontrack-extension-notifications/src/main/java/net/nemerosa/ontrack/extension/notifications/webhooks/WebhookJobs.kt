package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.NotificationsJobs
import net.nemerosa.ontrack.job.JobType

object WebhookJobs {

    val type: JobType = NotificationsJobs.category.getType("webhooks").withName("Webhooks")

}