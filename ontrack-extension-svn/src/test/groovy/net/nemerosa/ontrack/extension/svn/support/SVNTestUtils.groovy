package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import org.apache.commons.io.FileUtils

final class SVNTestUtils {

    private SVNTestUtils() {
    }

    static SVNRepository repository() {
        SVNRepository.of(
                1,
                new SVNConfiguration(
                        "test",
                        "svn://localhost",
                        "test",
                        "test",
                        "",
                        "",
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
