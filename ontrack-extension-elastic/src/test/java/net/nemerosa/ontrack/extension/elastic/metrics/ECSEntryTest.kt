package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.metrics.Metric
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ECSEntryTest {

    @Test
    fun `Two different metrics with the same tags do not have the same ID`() {
        val now = Time.now()
        val one = Metric(
            metric = uid("m-"),
            tags = mapOf(
                "project" to "my-project",
                "branch" to "my-branch",
            ),
            fields = mapOf(
                "value" to 2.0,
            ),
            timestamp = now,
        )
        val two = Metric(
            metric = uid("m-"),
            tags = mapOf(
                "project" to "my-project",
                "branch" to "my-branch",
            ),
            fields = mapOf(
                "value" to 2.0,
            ),
            timestamp = now,
        )
        assertTrue(
            one.toECSEntry().computeId() != two.toECSEntry().computeId(),
            "Two different metrics with the same tags do not have the same ID"
        )
    }

    @Test
    fun `Two metrics with different tags do not have the same ID`() {
        val now = Time.now()
        val metric = uid("m-")
        val one = Metric(
            metric = metric,
            tags = mapOf(
                "project" to "my-project",
                "branch" to "my-branch",
            ),
            fields = mapOf(
                "value" to 2.0,
            ),
            timestamp = now,
        )
        val two = Metric(
            metric = metric,
            tags = mapOf(
                "project" to "my-project",
                "branch" to "my-other-branch",
            ),
            fields = mapOf(
                "value" to 2.0,
            ),
            timestamp = now,
        )
        assertTrue(
            one.toECSEntry().computeId() != two.toECSEntry().computeId(),
            "Two metrics with different tags do not have the same ID"
        )
    }

    @Test
    fun `Two metrics with only different values have the same ID`() {
        val now = Time.now()
        val metric = uid("m-")
        val one = Metric(
            metric = metric,
            tags = mapOf(
                "project" to "my-project",
                "branch" to "my-branch",
            ),
            fields = mapOf(
                "value" to 2.0,
            ),
            timestamp = now,
        )
        val two = Metric(
            metric = metric,
            tags = mapOf(
                "project" to "my-project",
                "branch" to "my-branch",
            ),
            fields = mapOf(
                "value" to 5.0,
            ),
            timestamp = now,
        )
        assertEquals(
            one.toECSEntry().computeId(),
            two.toECSEntry().computeId(),
            "Two metrics with only different values have the same ID"
        )
    }

}