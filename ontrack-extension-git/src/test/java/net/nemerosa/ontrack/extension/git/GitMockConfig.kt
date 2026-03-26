package net.nemerosa.ontrack.extension.git

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile(GitMockConfig.Companion.PROFILE_GIT_MOCK)
class GitMockConfig {
    @Bean
    fun testGitRepositoryClient(): GitRepositoryClient {
        return mockk(relaxed = true)
    }

    @Bean
    @Primary
    fun repositoryClientFactory(): GitRepositoryClientFactory {
        val factory = mockk<GitRepositoryClientFactory>(relaxed = true)
        every {
            factory.getClient(
                any(),
                any()
            )
        } returns testGitRepositoryClient()
        return factory
    }

    companion object {
        const val PROFILE_GIT_MOCK: String = "git_mock"
    }
}
