package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractExtensionFeature implements ExtensionFeature {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionFeature.class);

    /**
     * Default version
     */
    public static final String VERSION_NONE = "none";

    private final String id;
    private final String name;
    private final String description;
    private final String version;
    private final ExtensionFeatureOptions options;

    public AbstractExtensionFeature(String id, String name, String description) {
        this(id, name, description, ExtensionFeatureOptions.DEFAULT);
    }

    public AbstractExtensionFeature(String id, String name, String description, ExtensionFeatureOptions options) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.options = options;
        // Reads descriptor from resources
        String path = String.format("/META-INF/ontrack/extension/%s.properties", id);
        logger.info("[extension][{}] Loading meta information at {}", id, path);
        InputStream in = getClass().getResourceAsStream(path);
        if (in != null) {
            try {
                try {
                    // Reads as properties
                    Properties properties = new Properties();
                    properties.load(in);
                    // Reads the version information
                    String value = properties.getProperty("version");
                    if (StringUtils.isNotBlank(value)) {
                        version = value;
                    } else {
                        logger.debug("[extension][{}] No version found in {} - using default version", id, path);
                        version = VERSION_NONE;
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(String.format("[extension][%s] Cannot read resource at %s", id, path), ex);
            }
        } else {
            logger.debug("[extension][{}] No meta information found at {} - using defaults", id, path);
            version = VERSION_NONE;
        }
        logger.info("[extension][{}] Version = {}", id, version);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ExtensionFeatureOptions getOptions() {
        return options;
    }
}
