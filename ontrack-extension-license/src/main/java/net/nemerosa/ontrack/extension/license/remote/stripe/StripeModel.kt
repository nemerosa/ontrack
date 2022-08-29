package net.nemerosa.ontrack.extension.license.remote.stripe

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StripeSubscription(
    override val id: String,
    val customer: String,
    val items: StripeCollection<StripeSubscriptionItem>,
    override val metadata: Map<String, String>,
    val status: String,
    @JsonProperty("current_period_end")
    val currentPeriodEnd: Long,
): StripeMetadataContainer {
    fun extractFirstPrice(): StripePrice =
        items.data.firstOrNull()?.price
            ?: error("Stripe subsccription has no price associated with it")
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StripeCustomer(
    val id: String,
    val name: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StripeCollection<T>(
    val data: List<T>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StripeSubscriptionItem(
    val id: String,
    val price: StripePrice,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StripePrice(
    override val id: String,
    override val metadata: Map<String, String>,
): StripeMetadataContainer

interface StripeEntity {
    val id: String
}

interface StripeMetadataContainer: StripeEntity {
    val metadata: Map<String, String>
}
