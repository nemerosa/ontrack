package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.settings.SettingsManager;
import net.nemerosa.ontrack.model.settings.SettingsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SettingsManagerServiceImpl implements SettingsManagerService {

    private final Collection<SettingsManager<?>> settingsManagers;

    @Autowired
    public SettingsManagerServiceImpl(Collection<SettingsManager<?>> settingsManagers) {
        this.settingsManagers = settingsManagers;
    }

    @Override
    public <T> void saveSettings(T settings) {
        Class<?> settingsClass = settings.getClass();
        for (SettingsManager<?> settingsManager : settingsManagers) {
            Class<?> managerClass = settingsManager.getSettingsClass();
            if (managerClass.isAssignableFrom(settingsClass)) {
                //noinspection unchecked
                saveSettings((SettingsManager<T>) settingsManager, settings);
            }
        }
    }

    private <T> void saveSettings(SettingsManager<T> settingsManager, T settings) {
        settingsManager.saveSettings(settings);
    }

}
