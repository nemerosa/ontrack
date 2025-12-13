package net.nemerosa.ontrack.extension.github

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

/**
 * Annotation to use on tests relying on an external GitHub repository.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.extension.github.TestOnGitHubCondition#isTestOnGitHubEnabled")
annotation class TestOnGitHub
