package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.BuildFilter;
import net.nemerosa.ontrack.model.structure.BuildFilterService;
import org.springframework.stereotype.Service;

@Service
public class BuildFilterServiceImpl implements BuildFilterService {

    @Override
    public BuildFilter defaultFilter() {
        return DefaultBuildFilter.INSTANCE;
    }

}
