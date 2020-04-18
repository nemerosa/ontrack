package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.Info
import net.nemerosa.ontrack.model.structure.InfoService
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("/rest/info")
class InfoController(
        private val infoService: InfoService,
        private val applicationInfoService: ApplicationInfoService
) : AbstractResourceController() {

    /**
     * General information about the application
     */
    @GetMapping("")
    fun info(): Resource<Info> {
        return Resource.of(
                infoService.info,
                uri(on(javaClass).info())
        )
                // API links
                .with("user", uri(on(UserController::class.java).currentUser))
                // Info message
                .with("_applicationInfo", uri(on(InfoController::class.java).applicationInfo()))
    }

    /**
     * Messages about the application
     */
    @GetMapping("application")
    fun applicationInfo(): Resources<ApplicationInfo> {
        return Resources.of(
                applicationInfoService.applicationInfoList,
                uri(on(InfoController::class.java).applicationInfo())
        )
    }

}