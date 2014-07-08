package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.ScheduledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ArtifactorySyncServiceImpl implements ScheduledService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public ArtifactorySyncServiceImpl(StructureService structureService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    protected void syncAll() {
        // FIXME For all projects
    }

    @Override
    public Runnable getTask() {
        return securityService.runAsAdmin(this::syncAll);
    }

    @Override
    public Trigger getTrigger() {
        return new PeriodicTrigger(1, TimeUnit.MINUTES);
    }
}
