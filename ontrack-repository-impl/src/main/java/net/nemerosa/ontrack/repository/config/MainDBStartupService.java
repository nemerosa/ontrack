package net.nemerosa.ontrack.repository.config;

import net.nemerosa.ontrack.model.support.StartupService;
import org.springframework.stereotype.Component;

/**
 * Place holder service.
 */
@Component
public class MainDBStartupService implements StartupService {

    @Override
    public String getName() {
        return "mainDb";
    }

    @Override
    public int startupOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void start() {
    }
}
