package net.nemerosa.ontrack.extension.git

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PullRequestStaleBranchCheckIT: AbstractGitTestSupport() {

    @Autowired
    private lateinit var check: PullRequestStaleBranchCheck

    @Test
    fun `Project not eligible if not configured for Git`() {
        project {
            assertFalse(check.isProjectEligible(this), "Project is not eligible for PR branch check because not on Git")
        }
    }

    @Test
    fun `Project not eligible if PR are not enabled`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                withPRDisabled {
                    assertFalse(check.isProjectEligible(this), "Project is not eligible for PR branch check because PR are not enabled")
                }
                assertTrue(check.isProjectEligible(this), "Project is eligible for PR branch check because PR are enabled")
            }
        }
    }

    @Test
    fun `Project not eligible if PR cleanup is not enabled`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                withPRCleanupDisabled {
                    assertFalse(check.isProjectEligible(this), "Project is not eligible for PR branch check because PR cleanup is not enabled")
                }
                assertTrue(check.isProjectEligible(this), "Project is eligible for PR branch check because PR cleanup is enabled")
            }
        }
    }

    @Test
    fun `Branch not eligible if not configured for Git`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    assertFalse(check.isBranchEligible(this), "Branch is not eligible for PR branch check because not on Git")
                }
            }
        }
    }

    @Test
    fun `Branch not eligible if not a PR`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    gitBranch("master")
                    assertFalse(check.isBranchEligible(this), "Branch is not eligible for PR branch check because not a PR")
                }
            }
        }
    }

    @Test
    fun `Branch not eligible if PR are not enabled`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    withPRDisabled {
                        assertFalse(check.isBranchEligible(this), "Branch is not eligible for PR branch check because PR are not enabled")
                    }
                    assertTrue(check.isBranchEligible(this), "Branch is eligible for PR branch check because PR are enabled")
                }
            }
        }
    }

    @Test
    fun `Branch not eligible if PR cleanup is not enabled`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                prGitProject(repo)
                branch {
                    gitBranch("PR-1")
                    withPRCleanupDisabled {
                        assertFalse(check.isBranchEligible(this), "Branch is not eligible for PR branch check because PR cleanup is not enabled")
                    }
                    assertTrue(check.isBranchEligible(this), "Branch is eligible for PR branch check because PR cleanup is enabled")
                }
            }
        }
    }

}