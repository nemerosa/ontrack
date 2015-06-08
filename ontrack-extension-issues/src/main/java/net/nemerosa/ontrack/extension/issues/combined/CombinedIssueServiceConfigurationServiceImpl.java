package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CombinedIssueServiceConfigurationServiceImpl implements CombinedIssueServiceConfigurationService {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public CombinedIssueServiceConfigurationServiceImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public List<CombinedIssueServiceConfiguration> getConfigurationList() {
        return configurationRepository.list(CombinedIssueServiceConfiguration.class);
    }

    @Override
    public Optional<CombinedIssueServiceConfiguration> getConfigurationByName(String name) {
        return configurationRepository.find(CombinedIssueServiceConfiguration.class, name);
    }
}
