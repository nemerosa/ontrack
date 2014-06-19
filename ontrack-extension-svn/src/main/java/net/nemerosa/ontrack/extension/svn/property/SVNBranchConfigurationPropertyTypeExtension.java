package net.nemerosa.ontrack.extension.svn.property;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNBranchConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<SVNBranchConfigurationProperty> {

    @Autowired
    public SVNBranchConfigurationPropertyTypeExtension(SVNExtensionFeature extensionFeature) {
        super(extensionFeature, new SVNBranchConfigurationPropertyType());
    }

}
