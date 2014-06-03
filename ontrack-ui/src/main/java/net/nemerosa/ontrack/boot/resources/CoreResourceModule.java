package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.controller.URIBuilder;

public class CoreResourceModule extends SimpleModule {

    private final URIBuilder uriBuilder;

    public CoreResourceModule(URIBuilder uriBuilder) {
        super("ontrack");
        this.uriBuilder = uriBuilder;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new BeanSerializerModifier() {

            public JsonSerializer<?> modifySerializer(
                    SerializationConfig config,
                    BeanDescription beanDesc,
                    JsonSerializer<?> serializer) {
                if (PromotionLevel.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new PromotionLevelResource((BeanSerializerBase) serializer, uriBuilder);
                } else {
                    return super.modifySerializer(config, beanDesc, serializer);
                }
            }
        });
    }
}
