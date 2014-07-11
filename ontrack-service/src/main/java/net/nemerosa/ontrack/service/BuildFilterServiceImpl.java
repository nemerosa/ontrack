package net.nemerosa.ontrack.service;

import com.google.common.collect.Maps;
import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PreferencesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuildFilterServiceImpl implements BuildFilterService {

    private final Collection<BuildFilterProvider> buildFilterProviders;
    private final PreferencesService preferencesService;
    private final BuildFilterPreferencesType preferencesType;

    @Autowired
    public BuildFilterServiceImpl(Collection<BuildFilterProvider> buildFilterProviders, PreferencesService preferencesService, BuildFilterPreferencesType preferencesType) {
        this.buildFilterProviders = buildFilterProviders;
        this.preferencesService = preferencesService;
        this.preferencesType = preferencesType;
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
                // Storage of filter
                String name = BuildFilterProvider.getParameter(parameters, "name");
                if (StringUtils.isNotBlank(name)) {
                    storeFilterInPreferences(optProvider.get(), name, parameters);
                }
                // Returns the filter
                return optProvider.get().filter(branchId, parameters);
            }
            // Returns a default filter
            else {
                return defaultFilter();
            }
        }
    }

    private void storeFilterInPreferences(BuildFilterProvider buildFilterProvider, String name, Map<String, String[]> parameters) {
        // Gets the previous preferences
        BuildFilterPreferences preferences = preferencesService.load(
                preferencesType,
                BuildFilterPreferences.empty()
        );
        // Builds the new preferences
        preferences = preferences.add(
                new BuildFilterPreferencesEntry(
                        name,
                        buildFilterProvider.getClass().getName(),
                        Maps.transformValues(
                                parameters,
                                Arrays::asList
                        )
                )
        );
        // Stores the preferences back
        preferencesService.store(preferencesType, preferences);
    }

    private Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

}
