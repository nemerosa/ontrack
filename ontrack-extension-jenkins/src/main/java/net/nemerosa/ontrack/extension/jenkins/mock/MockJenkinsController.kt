package net.nemerosa.ontrack.extension.jenkins.mock

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/extension/jenkins/mock")
@Profile(RunProfile.ACC)
class MockJenkinsController(
    private val mockJenkinsClientFactory: MockJenkinsClientFactory,
) {

    @GetMapping("/{config}/job")
    fun getJobByPath(@PathVariable config: String, @RequestParam path: String): MockJenkinsJob? =
        mockJenkinsClientFactory.findClient(config)?.jobs?.get(path)

}