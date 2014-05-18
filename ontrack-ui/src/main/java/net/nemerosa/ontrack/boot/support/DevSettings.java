package net.nemerosa.ontrack.boot.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ontrack.dev")
public class DevSettings {

    private String target;
    private String staticDir;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getStaticDir() {
        return staticDir;
    }

    public void setStaticDir(String staticDir) {
        this.staticDir = staticDir;
    }
    
}
