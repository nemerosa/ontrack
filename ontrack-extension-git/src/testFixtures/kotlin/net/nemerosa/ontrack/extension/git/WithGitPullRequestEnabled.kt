package net.nemerosa.ontrack.extension.git

import org.springframework.test.context.TestPropertySource

/**
 * When a test class or test function in annotated with this annotation,
 * the support for Git Pull Requests is enabled.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestPropertySource(
    properties = [
        "ontrack.config.extension.git.pullRequests.enabled=true",
    ]
)
annotation class WithGitPullRequestEnabled