package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static net.nemerosa.ontrack.boot.resource.Resource.link;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/ui")
public class UITop {

    private final ResourceAssembler resourceAssembler;

    @Autowired
    public UITop(ResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
    }

    /**
     * Root access point
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<Object> ui() {
        return Resource.empty()
                // TODO Version information
                // TODO Admin access point
                // Self
                .self(link(fromMethodCall(on(UITop.class).ui())))
                        // List of projects
                .link("projects", link(fromMethodCall(on(UIProject.class).projects())))
                ;
    }

}
