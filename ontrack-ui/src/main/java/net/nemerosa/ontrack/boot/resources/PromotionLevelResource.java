package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.boot.ui.PromotionLevelController;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.AbstractModelResource;
import net.nemerosa.ontrack.ui.resource.Link;

import java.io.IOException;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class PromotionLevelResource extends AbstractModelResource<PromotionLevel> {

    protected PromotionLevelResource(BeanSerializerBase src, URIBuilder uriBuilder) {
        super(src, uriBuilder);
    }

    @Override
    protected void additionalFields(PromotionLevel promotionLevel, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObjectField(Link.SELF, uri(on(PromotionLevelController.class).getPromotionLevel(promotionLevel.getId())));
        jgen.writeObjectField("branchLink", uri(on(BranchController.class).getBranch(promotionLevel.getBranch().getId())));
        jgen.writeObjectField("projectLink", uri(on(ProjectController.class).getProject(promotionLevel.getBranch().getProject().getId())));
        jgen.writeObjectField(Link.IMAGE_LINK, uri(on(PromotionLevelController.class).getPromotionLevelImage_(promotionLevel.getId())));
        // TODO Actions?
    }
}
