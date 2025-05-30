package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.test.TestUtils.uid

object LicenseFixtures {

    val sampleFeatureId = uid("f-")

    fun sampleProvidedFeature() = ProvidedLicensedFeature(
        id = sampleFeatureId,
        name = "Sample feature",
        alwaysEnabled = false,
    )

    fun sampleFeatureData() = LicenseFeatureData(
        id = sampleFeatureId,
        enabled = true,
        data = emptyList(),
    )

}