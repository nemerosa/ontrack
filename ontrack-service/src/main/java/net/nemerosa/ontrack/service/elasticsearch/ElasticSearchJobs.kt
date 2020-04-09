package net.nemerosa.ontrack.service.elasticsearch

import net.nemerosa.ontrack.job.JobCategory

object ElasticSearchJobs {

    val jobCategory = JobCategory("elasticsearch", "ElasticSearch")

    val indexationJobType = jobCategory.getType("indexation").withName("Search indexation")

    val indexationAllJobKey = indexationJobType.getKey("all")

}