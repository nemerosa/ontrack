package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class MockSCMBranchNotFoundException(name: String): NotFoundException("Mock SCM branch not found: $name")

class MockSCMPullRequestNotFoundException(): NotFoundException("Mock SCM pull request not found")
