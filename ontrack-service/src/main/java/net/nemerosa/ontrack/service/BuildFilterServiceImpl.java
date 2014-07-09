package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class BuildFilterServiceImpl implements BuildFilterService {

    private final Collection<BuildFilterProvider> buildFilterProviders;

    @Autowired
    public BuildFilterServiceImpl(Collection<BuildFilterProvider> buildFilterProviders) {
        this.buildFilterProviders = buildFilterProviders;
    }

    @Override
    public BuildFilter defaultFilter() {
        return DefaultBuildFilter.INSTANCE;
    }

    @Override
    public BuildFilters getBuildFilters(ID branchId) {
        return new BuildFilters(
                getBuildFilterForms(branchId),
                // TODO Existing filters
                Collections.emptyList()
        );
    }

    private Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

}
