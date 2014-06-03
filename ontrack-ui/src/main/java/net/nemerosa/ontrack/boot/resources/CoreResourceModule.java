package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

public class CoreResourceModule extends SimpleModule {

    private final ResourceContext resourceContext;

    public CoreResourceModule(ResourceContext resourceContext) {
        super("ontrack");
        this.resourceContext = resourceContext;
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
                    return new PromotionLevelResource((BeanSerializerBase) serializer, resourceContext);
                } else {
                    return super.modifySerializer(config, beanDesc, serializer);
                }
            }
        });
        context.addBeanSerializerModifier(new BeanSerializerModifier() {

            public JsonSerializer<?> modifySerializer(
                    SerializationConfig config,
                    BeanDescription beanDesc,
                    JsonSerializer<?> serializer) {
                if (Branch.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new BranchResource((BeanSerializerBase) serializer, resourceContext);
                } else {
                    return super.modifySerializer(config, beanDesc, serializer);
                }
            }
        });
        context.addBeanSerializerModifier(new BeanSerializerModifier() {

            public JsonSerializer<?> modifySerializer(
                    SerializationConfig config,
                    BeanDescription beanDesc,
                    JsonSerializer<?> serializer) {
                if (Project.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new ProjectResource((BeanSerializerBase) serializer, resourceContext);
                } else {
                    return super.modifySerializer(config, beanDesc, serializer);
                }
            }
        });
    }
}
