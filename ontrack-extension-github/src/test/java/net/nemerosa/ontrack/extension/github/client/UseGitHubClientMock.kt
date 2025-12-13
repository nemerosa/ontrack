package net.nemerosa.ontrack.extension.github.client

import org.springframework.test.context.TestPropertySource

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestPropertySource(
    properties = [
        "${OntrackGitHubClient.PROPERTY_GITHUB_CLIENT_TYPE}=${OntrackGitHubClient.PROPERTY_GITHUB_CLIENT_TYPE_MOCK}"
    ]
)
annotation class UseGitHubClientMock
