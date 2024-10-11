package net.nemerosa.ontrack.extension.license.remote.stripe

import net.nemerosa.ontrack.extension.license.License
import java.time.LocalDateTime
import java.time.ZoneOffset

data class StripeInformation(
    val customer: StripeCustomer,
    val subscription: StripeSubscription,
) {
    fun extractLicense(): License {
        val price = subscription.extractFirstPrice()
        return License(
            type = "Stripe",
            name = price.getMetadata(StripeMetadata.LICENSE_NAME),
            assignee = customer.name,
            validUntil = LocalDateTime.ofEpochSecond(subscription.currentPeriodEnd, 0, ZoneOffset.UTC),
            maxProjects = price.getMetadataInt(StripeMetadata.LICENSE_PROJECTS),
            active = subscription.extractActiveFlag(),
            // TODO Not available in Stripe yet
            features = emptyList(),
        )
    }
}