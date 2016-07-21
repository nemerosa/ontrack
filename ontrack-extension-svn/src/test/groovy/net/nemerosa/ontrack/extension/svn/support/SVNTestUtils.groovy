package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration

final class SVNTestUtils {

    private SVNTestUtils() {
    }

    static SVNRepository repository(String url) {
        SVNRepository.of(
                1,
                new SVNConfiguration(
                        "test",
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
