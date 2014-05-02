package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Project;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ui")
public class UITop {

    /**
     * Root access point
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource ui() {
        return Resource.empty()
                // TODO Version information
                // TODO Admin access point
                // TODO List of projects
                ;
    }

    /**
     * FIXME List of projects
     */
    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public List<Resource<Project>> projects() {
        return Collections.emptyList();
    }

}
