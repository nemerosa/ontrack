package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
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

    @Override
    public BuildFilter computeFilter(ID branchId, Map<String, String[]> parameters) {
        // TODO Storage of filter
        // Gets the type parameter
        String type = BuildFilterProvider.getParameter(parameters, "type");
        if (StringUtils.isBlank(type)) {
            return defaultFilter();
        } else {
            return buildFilterProviders.stream()
                    .filter(provider -> type.equals(provider.getClass().getName()))
                    .findFirst()
                    .map(provider -> provider.filter(branchId, parameters))
                    .orElse(defaultFilter());
        }
    }

    private Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

}
