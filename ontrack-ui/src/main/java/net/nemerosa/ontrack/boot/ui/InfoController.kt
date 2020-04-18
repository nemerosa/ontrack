package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.Info;
import net.nemerosa.ontrack.model.structure.InfoService;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/info")
public class InfoController extends AbstractResourceController {

    private final InfoService infoService;
    private final ApplicationInfoService applicationInfoService;

    @Autowired
    public InfoController(InfoService infoService, ApplicationInfoService applicationInfoService) {
        this.infoService = infoService;
        this.applicationInfoService = applicationInfoService;
    }

    /**
     * General information about the application
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<Info> info() {
        return Resource.of(
                infoService.getInfo(),
                uri(on(getClass()).info())
        )
                // API links
                .with("user", uri(on(UserController.class).getCurrentUser()))
                        // TODO Structure controller (--> projects, branches, etc.)
                        // Info message
                .with("_applicationInfo", uri(on(InfoController.class).applicationInfo()))
                ;
    }

    /**
     * Messages about the application
     */
    @RequestMapping(value = "application", method = RequestMethod.GET)
    public Resources<ApplicationInfo> applicationInfo() {
        return Resources.of(
                applicationInfoService.getApplicationInfoList(),
                uri(on(InfoController.class).applicationInfo())
        );
    }

}
