package net.nemerosa.ontrack.extension.jenkins.mock

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/extension/jenkins/mock")
@Profile(RunProfile.ACC)
class MockJenkinsController(
    private val client: MockJenkinsClient,
) {

    @GetMapping("/job")
    fun getJobByPath(@RequestParam path: String): MockJenkinsJob? =
        client.jobs[path]

}