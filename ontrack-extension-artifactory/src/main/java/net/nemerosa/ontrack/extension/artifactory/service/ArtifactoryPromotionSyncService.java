package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.model.structure.Branch;

public interface ArtifactoryPromotionSyncService {
    void scheduleArtifactoryBuildSync(Branch branch);

    void unscheduleArtifactoryBuildSync(Branch branch);
}
