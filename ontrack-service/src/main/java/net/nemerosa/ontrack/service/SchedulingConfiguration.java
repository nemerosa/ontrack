package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.support.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Collection;

@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

    private final Logger logger = LoggerFactory.getLogger(SchedulingConfiguration.class);

    @Autowired(required = false)
    private Collection<ScheduledService> scheduledServices;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        logger.info("[scheduling] Registering scheduling services...");
        if (scheduledServices != null) {
            for (ScheduledService scheduledService : scheduledServices) {
                logger.info("[scheduling] Registering scheduling service {}", scheduledService);
                taskRegistrar.addTriggerTask(
                        scheduledService.getTask(),
                        scheduledService.getTrigger()
                );
            }
        }
    }
}
