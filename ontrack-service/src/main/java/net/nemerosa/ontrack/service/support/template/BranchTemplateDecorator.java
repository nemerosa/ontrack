package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.structure.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BranchTemplateDecorator implements Decorator<BranchType> {

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
