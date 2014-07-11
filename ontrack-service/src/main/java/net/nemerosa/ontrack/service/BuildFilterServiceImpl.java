package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PreferencesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
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
                // Storage of filter
                String name = BuildFilterProvider.getParameter(parameters, "name");
                if (StringUtils.isNotBlank(name)) {
                    storeFilterInPreferences(branchId, optProvider.get(), name, parameters);
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

    private Collection<BuildFilterResource> loadExistingFilters(ID branchId) {
        // Gets the preferences and the list of entries for the branch
        Collection<BuildFilterPreferencesEntry> entries = preferencesService.load(
                preferencesType,
                BuildFilterPreferences.empty()
        ).getEntriesForBranch(branchId.getValue());

        return entries.stream()
                .map(prefEntry -> loadPrefEntry(branchId, prefEntry))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<BuildFilterResource> loadPrefEntry(ID branchId, BuildFilterPreferencesEntry prefEntry) {
        return buildFilterProviders.stream()
                .filter(provider -> StringUtils.equals(prefEntry.getType(), provider.getClass().getName()))
                .findFirst()
                .map(provider -> createBuildResource(branchId, provider, prefEntry.getName(), prefEntry.getData()));
    }

    private BuildFilterResource createBuildResource(ID branchId, BuildFilterProvider provider, String name, Map<String, String> data) {
        return new BuildFilterResource(
                name,
                provider.newFilterForm(branchId).with(data),
                data
        );
    }

    private void storeFilterInPreferences(ID branchId, BuildFilterProvider buildFilterProvider, String name, Map<String, String> parameters) {
        // Gets the previous preferences
        BuildFilterPreferences preferences = preferencesService.load(
                preferencesType,
                BuildFilterPreferences.empty()
        );
        // Builds the new preferences
        preferences = preferences.add(
                branchId.getValue(),
                new BuildFilterPreferencesEntry(
                        name,
                        buildFilterProvider.getClass().getName(),
                        parameters
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
