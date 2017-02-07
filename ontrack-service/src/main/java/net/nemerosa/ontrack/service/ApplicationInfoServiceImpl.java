package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import net.nemerosa.ontrack.model.support.ApplicationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    private final Logger logger = LoggerFactory.getLogger(ApplicationInfoService.class);

    private final List<ApplicationInfoProvider> providers;

    @Autowired
    public ApplicationInfoServiceImpl(List<ApplicationInfoProvider> providers) {
        this.providers = providers;
        providers.forEach(provider ->
                        logger.info("[info] Info provided: {}", provider.getClass().getName())
        );
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return providers.stream()
                .flatMap(provider -> provider.getApplicationInfoList().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
