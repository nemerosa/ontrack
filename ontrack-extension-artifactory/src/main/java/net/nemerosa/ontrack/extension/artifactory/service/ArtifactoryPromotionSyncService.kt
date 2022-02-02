package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.job.JobRunListener;
import net.nemerosa.ontrack.model.structure.Branch;

public interface ArtifactoryPromotionSyncService {

    void sync(Branch branch, JobRunListener listener);

    void syncBuild(Branch branch, String artifactoryBuildName, String buildName, ArtifactoryClient client, JobRunListener listener);

}
