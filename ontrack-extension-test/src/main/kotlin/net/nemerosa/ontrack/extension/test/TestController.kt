package net.nemerosa.ontrack.extension.test

import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/extension/test")
class TestController : AbstractResourceController() {
    /**
     * Stupid call to test that 3rd party components are correctly loaded in the
     * extension distribution (in this case, the Commons Math3 API).
     */
    @GetMapping("/3rdparty")
    fun thirdParty(@RequestParam value: Double, @RequestParam power: Double) = ThirdPartyResult(value, power)
}

data class ThirdPartyResult(val value: Double, val power: Double) {
    val result = Math.pow(value, power)
}
