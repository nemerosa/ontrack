package net.nemerosa.ontrack.extension.git.service

import org.springframework.stereotype.Component

/**
 * Local in-memory cache for counting the number of times a project sync failed because its remote did not exist.
 *
 * TODO V5 Use a real cache mechanism
 */
@Component
class GitNoRemoteCounter {

    private val counters = mutableMapOf<String, Int>()

    fun getNoRemoteCount(project: String) = counters[project] ?: 0

    fun resetNoRemoteCount(project: String) {
        counters.remove(project)
    }

    fun incNoRemoteCount(project: String) {
        val value = getNoRemoteCount(project)
        counters[project] = value + 1
    }

}