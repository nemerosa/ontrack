package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration

import static net.nemerosa.ontrack.test.TestUtils.uid

final class SVNTestUtils {

    private SVNTestUtils() {
    }

    static SVNRepository repository() {
        SVNRepository.of(
                1,
                new SVNConfiguration(
                        uid("C"),
                        "svn://localhost",
                        "test",
                        "test",
                        "",
                        "",
                        "",
                        "",
                        0,
                        1,
                        ""
                ),
                null
        )
    }
}
