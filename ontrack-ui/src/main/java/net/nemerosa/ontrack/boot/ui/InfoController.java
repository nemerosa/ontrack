package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.Info;
import net.nemerosa.ontrack.model.structure.InfoService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/info")
public class InfoController extends AbstractResourceController {

    private final InfoService infoService;

    @Autowired
    public InfoController(InfoService infoService) {
        this.infoService = infoService;
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
                .with("user", uri(on(UserAPIController.class).getCurrentUser()))
                // TODO Structure controller (--> projects, branches, etc.)
                // TODO Info message
                ;
    }
}
