package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class MockIssueServiceFeature extends AbstractExtensionFeature {

    MockIssueServiceFeature() {
        super('mock-issue', 'Mock issue', 'Mock issue service')
    }
}
