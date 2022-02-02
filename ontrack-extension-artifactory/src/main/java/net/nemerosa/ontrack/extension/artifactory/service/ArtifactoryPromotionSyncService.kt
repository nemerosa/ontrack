package net.nemerosa.ontrack.extension.artifactory.service

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.model.structure.Branch

interface ArtifactoryPromotionSyncService {

    fun sync(branch: Branch, listener: JobRunListener)

    fun syncBuild(
        branch: Branch,
        artifactoryBuildName: String,
        buildName: String,
        client: ArtifactoryClient,
        listener: JobRunListener,
    )

}