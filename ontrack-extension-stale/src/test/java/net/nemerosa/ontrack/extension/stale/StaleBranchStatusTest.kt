package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.extension.stale.StaleBranchStatus.Companion.min
import org.junit.Test
import kotlin.test.assertEquals

class StaleBranchStatusTest {

    @Test
    fun ordering() {
        assertEquals(StaleBranchStatus.KEEP, min(StaleBranchStatus.KEEP, StaleBranchStatus.KEEP))
        assertEquals(StaleBranchStatus.KEEP, min(StaleBranchStatus.KEEP, StaleBranchStatus.DISABLE))
        assertEquals(StaleBranchStatus.KEEP, min(StaleBranchStatus.KEEP, StaleBranchStatus.DELETE))
        assertEquals(StaleBranchStatus.KEEP, min(StaleBranchStatus.DISABLE, StaleBranchStatus.KEEP))
        assertEquals(StaleBranchStatus.DISABLE, min(StaleBranchStatus.DISABLE, StaleBranchStatus.DISABLE))
        assertEquals(StaleBranchStatus.DISABLE, min(StaleBranchStatus.DISABLE, StaleBranchStatus.DELETE))
        assertEquals(StaleBranchStatus.KEEP, min(StaleBranchStatus.DELETE, StaleBranchStatus.KEEP))
        assertEquals(StaleBranchStatus.DISABLE, min(StaleBranchStatus.DELETE, StaleBranchStatus.DISABLE))
        assertEquals(StaleBranchStatus.DELETE, min(StaleBranchStatus.DELETE, StaleBranchStatus.DELETE))
    }

}