package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.common.ProcessException
import net.nemerosa.ontrack.common.Utils
import org.springframework.test.annotation.ProfileValueSource

class SVNProfileValueSource implements ProfileValueSource {
    @Override
    String get(String key) {
        if ("svn" == key) {
            try {
                // Checks if the svn executable is available
                Utils.run new File('.'), 'svn', '--version'
                // OK
                return 'true'
            } catch (ProcessException ignored) {
                return 'false'
            }
        } else {
            return null
        }
    }
}
