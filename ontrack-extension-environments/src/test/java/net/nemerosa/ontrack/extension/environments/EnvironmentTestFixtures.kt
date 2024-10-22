package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.test.TestUtils.uid

object EnvironmentTestFixtures {
    fun testEnvironment(
        name: String = uid("env-"),
        order: Int = 1,
        tags: List<String> = emptyList(),
    ) = Environment(
        name = name,
        order = order,
        description = null,
        tags = tags,
    )
}