package net.nemerosa.ontrack.boot.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Data
@ConfigurationProperties(prefix = "ontrack.dev")
public class DevSettings {

    private File web = new File(System.getProperty("user.dir"), "ontrack-web");
    private String dev = "build/web/dev";
    private String prod = "build/web/prod";
    private String src = "src";
    private String vendor = "node_modules";

}
