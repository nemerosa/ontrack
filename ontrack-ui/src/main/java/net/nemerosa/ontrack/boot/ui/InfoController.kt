package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.Info
import net.nemerosa.ontrack.model.structure.InfoService
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/info")
class InfoController(
    private val infoService: InfoService,
    private val applicationInfoService: ApplicationInfoService
) {

    /**
     * General information about the application
     */
    @GetMapping("")
    fun info(): Info = infoService.info

    /**
     * Messages about the application
     */
    @GetMapping("application")
    fun applicationInfo(): List<ApplicationInfo> {
        return applicationInfoService.applicationInfoList
    }

}