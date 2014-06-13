package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import net.nemerosa.ontrack.model.support.ApplicationInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    private final List<ApplicationInfoProvider> providers;

    @Autowired
    public ApplicationInfoServiceImpl(ApplicationContext applicationContext) {
        this.providers = new ArrayList<>(applicationContext.getBeansOfType(ApplicationInfoProvider.class).values());
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        List<ApplicationInfo> messages = new ArrayList<>();
        for (ApplicationInfoProvider infoProvider : providers) {
            messages.addAll(infoProvider.getApplicationInfoList());
        }
        return messages;
    }
}
