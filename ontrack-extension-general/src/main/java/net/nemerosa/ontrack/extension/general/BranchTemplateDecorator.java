package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class BranchTemplateDecorator extends AbstractExtension implements DecorationExtension<BranchType> {

    @Autowired
    public BranchTemplateDecorator(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.BRANCH);
    }

    @Override
    public List<Decoration<BranchType>> getDecorations(ProjectEntity entity) {
        if (entity instanceof Branch) {
            Branch branch = (Branch) entity;
            switch (branch.getType()) {
                case TEMPLATE_DEFINITION:
                case TEMPLATE_INSTANCE:
                    return Collections.singletonList(
                            Decoration.of(
                                    this,
                                    branch.getType()
                            )
                    );
                default:
                    // No decoration by default
                    return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

}
