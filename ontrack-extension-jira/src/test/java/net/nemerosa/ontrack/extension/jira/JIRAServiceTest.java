package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.tx.JIRASession;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class JIRAServiceTest {

    private ConfigurationRepository configurationRepository;
    private JIRAConfigurationService jiraService;
    private EncryptionService encryptionService;

    @Before
    public void before() {
        SecurityService securityService = mock(SecurityService.class);
        configurationRepository = mock(ConfigurationRepository.class);
        encryptionService = mock(EncryptionService.class);

        JIRASession jiraSession = mock(JIRASession.class);
        when(jiraSession.getClient()).thenReturn(mock(JIRAClient.class));

        JIRASessionFactory jiraSessionFactory = mock(JIRASessionFactory.class);
        when(jiraSessionFactory.create(any(JIRAConfiguration.class))).thenReturn(jiraSession);

        OntrackConfigProperties ontrackConfigProperties = new OntrackConfigProperties();

        jiraService = new JIRAConfigurationServiceImpl(configurationRepository, securityService, encryptionService,
                mock(EventPostService.class), mock(EventFactory.class), jiraSessionFactory, ontrackConfigProperties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_name_check() {
        jiraService.updateConfiguration("test", new JIRAConfiguration("Test", "http://host", "user", ""));
    }

    @Test
    public void update_blank_password() {
        when(encryptionService.encrypt("secret")).thenReturn("xxxxx");
        when(encryptionService.decrypt("xxxxx")).thenReturn("secret");
        when(configurationRepository.find(JIRAConfiguration.class, "test")).thenReturn(
                Optional.of(
                        new JIRAConfiguration("test", "http://host", "user", "xxxxx")
                )
        );
        jiraService.updateConfiguration("test", new JIRAConfiguration("test", "http://host", "user", ""));
        verify(configurationRepository, times(1)).save(new JIRAConfiguration("test", "http://host", "user", "xxxxx"));
    }

    @Test
    public void update_blank_password_for_different_user() {
        when(encryptionService.encrypt("")).thenReturn("xxxxx");
        when(configurationRepository.find(JIRAConfiguration.class, "test")).thenReturn(
                Optional.of(
                        new JIRAConfiguration("test", "http://host", "user", "xxxxx")
                )
        );
        jiraService.updateConfiguration("test", new JIRAConfiguration("test", "http://host", "user1", ""));
        verify(configurationRepository, times(1)).save(new JIRAConfiguration("test", "http://host", "user1", "xxxxx"));
    }

    @Test
    public void update_new_password() {
        when(encryptionService.encrypt("pwd")).thenReturn("xxxxx");
        when(configurationRepository.find(JIRAConfiguration.class, "test")).thenReturn(
                Optional.of(
                        new JIRAConfiguration("test", "http://host", "user", "xxxxx")
                )
        );
        jiraService.updateConfiguration("test", new JIRAConfiguration("test", "http://host", "user", "pwd"));
        verify(configurationRepository, times(1)).save(new JIRAConfiguration("test", "http://host", "user", "xxxxx"));
    }

}
