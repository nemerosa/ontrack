package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

@AsAdminTest
class OptionalVersionBranchOrderingIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var ordering: OptionalVersionBranchOrdering

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Test
    fun `Match on branch path first`() {
        mockSCMTester.withMockSCMRepository {
            project {
                branch {
                    configureMockSCMBranch("release/1.0")
                    assertEquals(
                        "1.0.0",
                        ordering.getVersion(this, "release/.*".toRegex()).version?.toString()
                    )
                }
            }
        }
    }

    @Test
    fun `Match on branch name`() {
        mockSCMTester.withMockSCMRepository {
            project {
                branch("release-1.0") {
                    assertNull(
                        ordering.getVersion(this, "release/.*".toRegex()).version
                    )
                }
            }
        }
    }

    @Test
    fun `Ordering based on version after separator even if no group specified`() {
        mockSCMTester.withMockSCMRepository {
            project {
                val b10 = branch("release-1.0") {
                    configureMockSCMBranch("release/1.0")
                }
                val b01 = branch("release-0.1") {
                    configureMockSCMBranch("release/0.1")
                }
                val comparator = ordering.getComparator("release/.*")
                val branch = listOf(b10, b01).minWithOrNull(comparator)?.name
                assertEquals("release-1.0", branch)
            }
        }
    }

    @Test
    fun `Ordering based on version in group`() {
        mockSCMTester.withMockSCMRepository {
            project {
                val b10 = branch("release-1.0") {
                    configureMockSCMBranch("release/1.0")
                }
                val b01 = branch("release-0.1") {
                    configureMockSCMBranch("release/0.1")
                }
                val comparator = ordering.getComparator("release/(.*)")
                val branch = listOf(b10, b01).minWithOrNull(comparator)?.name
                assertEquals("release-1.0", branch)
            }
        }
    }

    @Test
    fun `Ordering based on name if no version available`() {
        mockSCMTester.withMockSCMRepository {
            project {
                val b10 = branch("release-1.0") {
                    configureMockSCMBranch("release/1.0")
                }
                val b01 = branch("release-0.1") {
                    configureMockSCMBranch("release/0.1")
                }
                val comparator = ordering.getComparator("maintenance/.*")
                val branch = listOf(b10, b01).minWithOrNull(comparator)?.name
                // Here, because there is no match on version, 1.0 becomes the first branch because of its name only
                assertEquals("release-1.0", branch)
            }
        }
    }

}