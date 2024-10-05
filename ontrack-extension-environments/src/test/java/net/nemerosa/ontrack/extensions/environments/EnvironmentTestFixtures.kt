package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.test.TestUtils.uid

object EnvironmentTestFixtures {
    fun testEnvironment(
        name: String = uid("env-"),
        order: Int = 1,
    ) = Environment(
        name = name,
        order = order,
        description = null,
    )
}