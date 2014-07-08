package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import net.nemerosa.ontrack.model.support.ApplicationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    private final List<ApplicationInfoProvider> providers;

    @Autowired
    public ApplicationInfoServiceImpl(ApplicationContext applicationContext) {
        this.providers = new ArrayList<>(applicationContext.getBeansOfType(ApplicationInfoProvider.class).values());
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return providers.stream()
                .flatMap(provider -> provider.getApplicationInfoList().stream())
                .filter(info -> info != null)
                .collect(Collectors.toList());
    }
}
