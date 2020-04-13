package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PreferencesService;
import net.nemerosa.ontrack.model.structure.PreferencesType;
import net.nemerosa.ontrack.repository.PreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PreferencesServiceImpl implements PreferencesService {

    private final PreferencesRepository repository;
    private final SecurityService securityService;

    @Autowired
    public PreferencesServiceImpl(PreferencesRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    @Override
    public <T> T load(PreferencesType<T> preferencesType, T defaultValue) {
        // Gets the current account
        OntrackAuthenticatedUser account = securityService.getCurrentAccount();
        if (account != null) {
            return repository.find(account.id(), preferencesType.getClass().getName())
                    .map(preferencesType::fromStorage)
                    .orElse(defaultValue);
        } else {
            // Not logged
            return defaultValue;
        }
    }

    @Override
    public <T> void store(PreferencesType<T> preferencesType, T value) {
        // Gets the current account
        OntrackAuthenticatedUser account = securityService.getCurrentAccount();
        if (account != null) {
            repository.store(
                    account.id(),
                    preferencesType.getClass().getName(),
                    preferencesType.forStorage(value)
            );
        }
    }
}
