package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Project;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static net.nemerosa.ontrack.boot.resource.Link.of;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/ui")
public class UITop {

    /**
     * Root access point
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<Object> ui() {
        return Resource.empty()
                // TODO Version information
                // TODO Admin access point
                // Self
                .self(of(fromMethodCall(on(UITop.class).ui())))
                        // List of projects
                .link("projects", of(fromMethodCall(on(UITop.class).projects())))
                ;
    }

    /**
     * FIXME List of projects
     */
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public List<Resource<Project>> projects() {
        return Collections.emptyList();
    }

}
