package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.job.JobInfoListener;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyCleanupEventListener implements EventListener {

    private final PropertyService propertyService;

    @Autowired
    public PropertyCleanupEventListener(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public void onEvent(Event event) {
        // FIXME Method net.nemerosa.ontrack.service.support.PropertyCleanupEventListener.onEvent
    }

    protected void cleanup(JobInfoListener info) {
        propertyService.getPropertyTypes().forEach(propertyType -> cleanPropertyType(propertyType, info));
    }

    protected <T> void cleanPropertyType(PropertyType<T> propertyType, JobInfoListener info) {
        // FIXME Method net.nemerosa.ontrack.service.support.PropertyCleanupJob.cleanPropertyType
    }
}
