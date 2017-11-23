package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.exceptions.CannotCreateWorkingDirException;
import net.nemerosa.ontrack.model.structure.VersionInfo;
import net.nemerosa.ontrack.model.support.EnvService;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import net.nemerosa.ontrack.model.support.VersionInfoConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class EnvServiceImpl implements EnvService {

    private final Logger logger = LoggerFactory.getLogger(EnvService.class);
    private final String defaultProfiles;
    private final String profiles;
    private final VersionInfo version;
    private final String jdbcUrl;
    private final File home;

    @Autowired
    public EnvServiceImpl(VersionInfoConfig version,
                          OntrackConfigProperties configProperties,
                          ApplicationContext ctx) {
        this.defaultProfiles = StringUtils.join(ctx.getEnvironment().getDefaultProfiles(), ",");
        this.profiles = StringUtils.join(ctx.getEnvironment().getActiveProfiles(), ",");
        // Version information from the configuration
        this.version = version.toInfo();
        // JDBC URL
        this.jdbcUrl = ctx.getEnvironment().getProperty("spring.datasource.url");
        // Home directory
        this.home = new File(configProperties.getApplicationWorkingDir());
    }

    @Override
    public String getProfiles() {
        return profiles;
    }

    @Override
    public VersionInfo getVersion() {
        return version;
    }

    @Override
    public File getWorkingDir(String context, String name) {
        File cxd = new File(home, context);
        File wd = new File(cxd, name);
        try {
            FileUtils.forceMkdir(wd);
        } catch (IOException e) {
            throw new CannotCreateWorkingDirException(wd, e);
        }
        return wd;
    }

    @PostConstruct
    public void init() throws FileNotFoundException {
        logger.info("[env] With JDK:              {}", System.getProperty("java.version"));
        logger.info("[env] With default profiles: {}", defaultProfiles);
        logger.info("[env] With active profiles:  {}", profiles);
        logger.info("[datasource] URL:            {}", jdbcUrl);
        logger.info("[version] Display:           {}", version.getDisplay());
        logger.info("[version] Full:              {}", version.getFull());
        logger.info("[version] Branch:            {}", version.getBranch());
        logger.info("[version] Build:             {}", version.getBuild());
        logger.info("[version] Commit:            {}", version.getCommit());
        logger.info("[version] Source:            {}", version.getSource());
        logger.info("[version] Source type:       {}", version.getSourceType());
    }
}
