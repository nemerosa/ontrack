package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.lang.String.format;

@Service
public class ApplicationLogServiceImpl implements ApplicationLogService {

    private final Logger logger = LoggerFactory.getLogger(ApplicationLogService.class);

    @Override
    public void error(Throwable exception, String service, Map<String, ?> info, String message, Object... messageParams) {
        // Message to log
        String log = format(
                "[application] service=%s,%s,message=%s",
                service,
                info,
                format(message, messageParams)
        );
        // Logging
        logger.error(log, exception);
    }

}
