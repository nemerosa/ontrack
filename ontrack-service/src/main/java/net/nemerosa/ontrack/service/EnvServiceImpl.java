package net.nemerosa.ontrack.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;

@Service
public class EnvServiceImpl implements EnvService {

    private final Logger logger = LoggerFactory.getLogger(EnvService.class);
    private final String profiles;
    private final String version;

    @Autowired
    public EnvServiceImpl(@Value("${app.version}") String version,
                          ApplicationContext ctx) {
        this.profiles = StringUtils.join(ctx.getEnvironment().getActiveProfiles(), ",");
        this.version = version;
    }

    @Override
    public String getProfiles() {
        return profiles;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @PostConstruct
    public void init() throws FileNotFoundException {
        logger.info("[env] With JDK:      {}", System.getProperty("java.version"));
        logger.info("[env] With profiles: {}", profiles);
        logger.info("[env] With version:  {}", version);
    }
}
