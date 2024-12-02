package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.test.TestUtils.uid

object LicenseFixtures {

    fun sampleFeatureData() = LicenseFeatureData(
        id = uid("f-"),
        enabled = true,
        data = emptyList(),
    )

}