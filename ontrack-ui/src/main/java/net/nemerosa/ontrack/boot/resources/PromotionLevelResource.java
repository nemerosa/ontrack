package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.boot.ui.PromotionLevelController;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.controller.URIBuilder;

import java.io.IOException;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class PromotionLevelResource extends AbstractModelResource<PromotionLevel> {

    protected PromotionLevelResource(BeanSerializerBase src, URIBuilder uriBuilder) {
        super(src, uriBuilder);
    }

    @Override
    protected void additionalFields(PromotionLevel bean, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObjectField("imageLink", uri(on(PromotionLevelController.class).getPromotionLevelImage_(bean.getId())));

    }
}
