package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.MainBuildLinksConfig
import net.nemerosa.ontrack.model.labels.MainBuildLinksProvider
import net.nemerosa.ontrack.model.labels.MainBuildLinksService
import net.nemerosa.ontrack.model.labels.ProvidedMainBuildLinksConfig
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MainBuildLinksServiceImpl(
        private val mainBuildLinksProviders: List<MainBuildLinksProvider>
) : MainBuildLinksService {
    override fun getMainBuildLinksConfig(project: Project): MainBuildLinksConfig {
        // List of all provided configurations
        return mainBuildLinksProviders.map { provider ->
            provider.getMainBuildLinksConfig(project)
        }
                // Order them by increasing order
                .sortedBy { it.order }
                // Folding from left to right
                .fold(ProvidedMainBuildLinksConfig.empty) { current, new ->
                    new.mergeInto(current)
                }
                // As a configuration
                .toMainBuildLinksConfig()
    }
}
