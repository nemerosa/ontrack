package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.form.DescribedForm;

/**
 * Extension that defines a contribution to the global settings.
 */
public interface GlobalSettingsExtension extends GlobalExtension {

    DescribedForm getConfigurationForm();

}
