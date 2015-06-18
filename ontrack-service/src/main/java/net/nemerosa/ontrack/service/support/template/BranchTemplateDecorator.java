package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BranchTemplateDecorator implements Decorator {

    @Override
    public List<Decoration> getDecorations(ProjectEntity entity) {
        if (entity instanceof Branch) {
            Branch branch = (Branch) entity;
            switch (branch.getType()) {
                case TEMPLATE_DEFINITION:
                    return Collections.singletonList(
                            Decoration.of(
                                    this,
                                    "definition",
                                    "Template definition"
                            )
                    );
                case TEMPLATE_INSTANCE:
                    return Collections.singletonList(
                            Decoration.of(
                                    this,
                                    "instance",
                                    "Template instance"
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
