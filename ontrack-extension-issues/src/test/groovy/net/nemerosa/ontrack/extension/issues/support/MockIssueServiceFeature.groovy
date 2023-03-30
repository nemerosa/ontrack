package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Deprecated
@Component
class MockIssueServiceFeature extends AbstractExtensionFeature {

    MockIssueServiceFeature() {
        super('mock-issue', 'Mock issue', 'Mock issue service')
    }
}
