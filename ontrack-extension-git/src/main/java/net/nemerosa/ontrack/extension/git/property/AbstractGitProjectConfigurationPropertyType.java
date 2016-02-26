package net.nemerosa.ontrack.extension.git.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

public abstract class AbstractGitProjectConfigurationPropertyType<T> extends AbstractPropertyType<T> {

    private final GitService gitService;

    protected AbstractGitProjectConfigurationPropertyType(ExtensionFeature extensionFeature, GitService gitService) {
        super(extensionFeature);
        this.gitService = gitService;
    }

    @Override
    public void onPropertyChanged(ProjectEntity entity, T value) {
        gitService.scheduleGitIndexation(getGitConfiguration(value));
    }

    @Override
    public void onPropertyDeleted(ProjectEntity entity, T oldValue) {
        gitService.unscheduleGitIndexation(getGitConfiguration(oldValue));
    }

    protected abstract GitConfiguration getGitConfiguration(T value);

}
