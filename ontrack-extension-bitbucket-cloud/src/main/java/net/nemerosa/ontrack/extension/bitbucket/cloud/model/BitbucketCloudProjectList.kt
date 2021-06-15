package net.nemerosa.ontrack.extension.bitbucket.cloud.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * List of projects.
 *
 * @property values Actual list of projects
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BitbucketCloudProjectList(
    val values: List<BitbucketCloudProject>
)
