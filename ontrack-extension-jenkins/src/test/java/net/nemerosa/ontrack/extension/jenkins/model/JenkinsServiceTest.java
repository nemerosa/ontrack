package net.nemerosa.ontrack.extension.jenkins.model;

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService;
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationServiceImpl;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class JenkinsServiceTest {

    private ConfigurationRepository configurationRepository;
    private JenkinsConfigurationService jenkinsService;
    private EncryptionService encryptionService;

    @Before
    public void before() {
        SecurityService securityService = mock(SecurityService.class);
        configurationRepository = mock(ConfigurationRepository.class);
        encryptionService = mock(EncryptionService.class);
        jenkinsService = new JenkinsConfigurationServiceImpl(configurationRepository, securityService, encryptionService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_name_check() {
        jenkinsService.updateConfiguration("test", new JenkinsConfiguration("Test", "http://host", "user", ""));
    }

    @Test
    public void update_blank_password() {
        when(encryptionService.encrypt("secret")).thenReturn("xxxxx");
        when(encryptionService.decrypt("xxxxx")).thenReturn("secret");
        when(configurationRepository.find(JenkinsConfiguration.class, "test")).thenReturn(
                Optional.of(
                        new JenkinsConfiguration("test", "http://host", "user", "xxxxx")
                )
        );
        jenkinsService.updateConfiguration("test", new JenkinsConfiguration("test", "http://host", "user", ""));
        verify(configurationRepository, times(1)).save(new JenkinsConfiguration("test", "http://host", "user", "xxxxx"));
    }

    @Test
    public void update_blank_password_for_different_user() {
        when(encryptionService.encrypt("")).thenReturn("xxxxx");
        when(configurationRepository.find(JenkinsConfiguration.class, "test")).thenReturn(
                Optional.of(
                        new JenkinsConfiguration("test", "http://host", "user", "xxxxx")
                )
        );
        jenkinsService.updateConfiguration("test", new JenkinsConfiguration("test", "http://host", "user1", ""));
        verify(configurationRepository, times(1)).save(new JenkinsConfiguration("test", "http://host", "user1", "xxxxx"));
    }

    @Test
    public void update_new_password() {
        when(encryptionService.encrypt("pwd")).thenReturn("xxxxx");
        jenkinsService.updateConfiguration("test", new JenkinsConfiguration("test", "http://host", "user", "pwd"));
        verify(configurationRepository, times(0)).find(JenkinsConfiguration.class, "test");
        verify(configurationRepository, times(1)).save(new JenkinsConfiguration("test", "http://host", "user", "xxxxx"));
    }

}
