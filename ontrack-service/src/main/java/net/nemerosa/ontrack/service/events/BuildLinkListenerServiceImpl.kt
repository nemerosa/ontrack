package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.BuildLinkListener
import net.nemerosa.ontrack.model.events.BuildLinkListenerService
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class BuildLinkListenerServiceImpl(
        applicationContext: ApplicationContext
) : BuildLinkListenerService {

    private val listeners: Collection<BuildLinkListener> by lazy {
        applicationContext.getBeansOfType(BuildLinkListener::class.java).values
    }

    override fun onBuildLinkAdded(from: Build, to: Build) {
        listeners.forEach { it.onBuildLinkAdded(from, to) }
    }

    override fun onBuildLinkDeleted(from: Build, to: Build) {
        listeners.forEach { it.onBuildLinkDeleted(from, to) }
    }
}