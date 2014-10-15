package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.exceptions.TemplateSynchronisationSourceNotFoundException;
import net.nemerosa.ontrack.model.structure.TemplateSynchronisationService;
import net.nemerosa.ontrack.model.structure.TemplateSynchronisationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateSynchronisationServiceImpl implements TemplateSynchronisationService {

    private final List<TemplateSynchronisationSource<?>> synchronisationSources;

    @Autowired
    public TemplateSynchronisationServiceImpl(List<TemplateSynchronisationSource<?>> synchronisationSources) {
        this.synchronisationSources = synchronisationSources;
    }

    @Override
    public List<TemplateSynchronisationSource<?>> getSynchronisationSources() {
        return synchronisationSources;
    }

    @Override
    public TemplateSynchronisationSource<?> getSynchronisationSource(String id) {
        return synchronisationSources.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElseThrow(() -> new TemplateSynchronisationSourceNotFoundException(id));
    }
}
