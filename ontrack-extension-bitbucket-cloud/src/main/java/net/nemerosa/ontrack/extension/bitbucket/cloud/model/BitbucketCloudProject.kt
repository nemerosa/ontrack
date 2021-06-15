package net.nemerosa.ontrack.extension.bitbucket.cloud.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Representation of a "project" in Bitbucket Cloud
 *
 * @property uuid Technical ID for the project
 * @property key Short key for the project
 * @property name Display name for the project
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BitbucketCloudProject(
    val uuid: String,
    val key: String,
    val name: String,
)