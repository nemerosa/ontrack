package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration

import static net.nemerosa.ontrack.test.TestUtils.uid

final class SVNTestUtils {

    private SVNTestUtils() {
    }

    static SVNRepository repository(String url, String name = null) {
        SVNRepository.of(
                1,
                new SVNConfiguration(
                        name ?: uid('S'),
                        url,
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
