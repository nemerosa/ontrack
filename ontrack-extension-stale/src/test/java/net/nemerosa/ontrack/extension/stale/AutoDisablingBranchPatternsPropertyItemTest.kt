package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AutoDisablingBranchPatternsPropertyItemTest {

    @Test
    fun `No includes always matches`() {
        assertTrue(
            AutoDisablingBranchPatternsPropertyItem().matches(uid(""))
        )
    }

    @Test
    fun `No matching include`() {
        assertFalse(
            AutoDisablingBranchPatternsPropertyItem(
                includes = listOf("main"),
            ).matches(uid(""))
        )
    }

    @Test
    fun `Matching include`() {
        assertTrue(
            AutoDisablingBranchPatternsPropertyItem(
                includes = listOf("develop", "release-.*"),
            ).matches(uid("release-1.22"))
        )
    }

    @Test
    fun `Matching include and no matching exclude`() {
        assertTrue(
            AutoDisablingBranchPatternsPropertyItem(
                includes = listOf("develop", "release-.*"),
                excludes = listOf("release-0.*")
            ).matches(uid("release-1.22"))
        )
    }

    @Test
    fun `Matching include and matching exclude`() {
        assertFalse(
            AutoDisablingBranchPatternsPropertyItem(
                includes = listOf("develop", "release-.*"),
                excludes = listOf("release-0.*")
            ).matches(uid("release-0.22"))
        )
    }

}