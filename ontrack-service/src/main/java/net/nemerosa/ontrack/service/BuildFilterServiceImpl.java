package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
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
                loadExistingFilters(branchId)
        );
    }

    @Override
    public BuildFilter computeFilter(ID branchId, Map<String, String> parameters) {
        // Gets the type parameter
        String type = BuildFilterProvider.getParameter(parameters, "type");
        if (StringUtils.isBlank(type)) {
            return defaultFilter();
        } else {
            // Gets the provider
            Optional<BuildFilterProvider> optProvider = buildFilterProviders.stream()
                    .filter(provider -> type.equals(provider.getClass().getName()))
                    .findFirst();
            // If found
            if (optProvider.isPresent()) {
                // Returns the filter
                return optProvider.get().filter(branchId, parameters);
            }
            // Returns a default filter
            else {
                return defaultFilter();
            }
        }
    }

    private Collection<BuildFilterResource> loadExistingFilters(ID branchId) {
        // TODO Loads the existing filters
        return Collections.emptyList();
    }

    private void storeFilter(ID branchId, BuildFilterProvider buildFilterProvider, String name, Map<String, String> parameters) {
        // TODO Stores the build filter
    }

    private Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

}
