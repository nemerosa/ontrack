package net.nemerosa.ontrack.extension.issues.mock

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class TestIssueServiceFeature : AbstractExtensionFeature("test-issue", "Mock issue", "Mock issue service")
