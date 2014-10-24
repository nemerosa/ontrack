package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class BranchTemplateDecorator implements Decorator {

    @Override
    public Decoration getDecoration(ProjectEntity entity) {
        if (entity instanceof Branch) {
            Branch branch = (Branch) entity;
            switch (branch.getType()) {
                case TEMPLATE_DEFINITION:
                    return Decoration.of(
                            this,
                            "definition",
                            "Template definition"
                    );
                case TEMPLATE_INSTANCE:
                    return Decoration.of(
                            this,
                            "instance",
                            "Template instance"
                    );
                default:
                    // No decoration by default
                    return null;
            }
        } else {
            return null;
        }
    }

}
