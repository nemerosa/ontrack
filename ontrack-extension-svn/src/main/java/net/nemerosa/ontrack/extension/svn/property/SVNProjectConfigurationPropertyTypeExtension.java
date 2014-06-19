package net.nemerosa.ontrack.extension.svn.property;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import net.nemerosa.ontrack.extension.svn.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNProjectConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<SVNProjectConfigurationProperty> {

    @Autowired
    public SVNProjectConfigurationPropertyTypeExtension(SVNExtensionFeature extensionFeature, SVNConfigurationService configurationService) {
        super(extensionFeature, new SVNProjectConfigurationPropertyType(configurationService));
    }

}
