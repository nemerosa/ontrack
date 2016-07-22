package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.test.TestUtils

final class SVNTestUtils {

    private SVNTestUtils() {
    }

    static SVNRepository repository(String url, String name = null) {
        SVNRepository.of(
                1,
                new SVNConfiguration(
                        name ?: TestUtils.uid('S'),
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
