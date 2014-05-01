package net.nemerosa.ontrack.boot.ui;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UITop {

    /**
     * Displays the version and other access points.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String ui() {
        return "OK";
    }

}
