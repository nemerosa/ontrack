package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.LastRevisionInfo;
import net.nemerosa.ontrack.job.JobType;
import net.nemerosa.ontrack.model.Ack;

public interface IndexationService {

    JobType INDEXATION_JOB = SVNService.SVN_JOB_CATEGORY
            .getType("svn-indexation")
            .withName("SVN Indexation");

    Ack indexFromLatest(String name);

    Ack reindex(String name);

    LastRevisionInfo getLastRevisionInfo(String name);
}
