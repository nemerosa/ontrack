package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.*;
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotFoundException;
import net.nemerosa.ontrack.model.exceptions.BuildFilterNotLoggedException;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.BuildFilterRepository;
import net.nemerosa.ontrack.repository.TBuildFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildFilterServiceImpl implements BuildFilterService {

    private final Collection<BuildFilterProvider<?>> buildFilterProviders;
    private final BuildFilterRepository buildFilterRepository;
    private final SecurityService securityService;

    @Autowired
    public BuildFilterServiceImpl(
            Collection<BuildFilterProvider<?>> buildFilterProviders,
            BuildFilterRepository buildFilterRepository,
            SecurityService securityService) {
        this.buildFilterProviders = buildFilterProviders;
        this.buildFilterRepository = buildFilterRepository;
        this.securityService = securityService;
    }

    @Override
    public BuildFilter defaultFilter() {
        return DefaultBuildFilter.INSTANCE;
    }

    @Override
    public Collection<BuildFilterResource<?>> getBuildFilters(ID branchId) {
        // Are we logged?
        Account account = securityService.getCurrentAccount();
        if (account != null) {
            // Gets the filters for this account and the branch
            return buildFilterRepository.findForBranch(account.id(), branchId.getValue()).stream()
                    .map(this::loadBuildFilterResource)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        // Not logged, no filter
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<BuildFilterForm> getBuildFilterForms(ID branchId) {
        return buildFilterProviders.stream()
                .map(provider -> provider.newFilterForm(branchId))
                .collect(Collectors.toList());
    }

    @Override
    public BuildFilter computeFilter(ID branchId, String type, JsonNode jsonData) {
        Optional<BuildFilter> optFilter = getBuildFilterProviderByType(type).map(provider -> getBuildFilter(branchId, provider, jsonData));
        return optFilter.get();
    }

    @Override
    public BuildFilterForm getEditionForm(ID branchId, String name) throws BuildFilterNotFoundException {
        return securityService.getAccount()
                .flatMap(account -> buildFilterRepository.findByBranchAndName(account.id(), branchId.getValue(), name))
                .flatMap(this::getBuildFilterForm)
                .orElseThrow(BuildFilterNotLoggedException::new);
    }

    private <T> Optional<BuildFilterForm> getBuildFilterForm(TBuildFilter t) {
        Optional<? extends BuildFilterProvider<T>> optProvider = getBuildFilterProviderByType(t.getType());
        if (optProvider.isPresent()) {
            return optProvider.get().parse(t.getData()).map(data -> optProvider.get().getFilterForm(
                    ID.of(t.getBranchId()),
                    data
            ));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Ack saveFilter(ID branchId, String name, String type, JsonNode parameters) {
        // TODO Excludes predefined filters
        // Checks the account
        Account account = securityService.getCurrentAccount();
        if (account == null) {
            return Ack.NOK;
        }
        // Checks the provider
        Optional<? extends BuildFilterProvider<Object>> provider = getBuildFilterProviderByType(type);
        if (!provider.isPresent()) {
            return Ack.NOK;
        }
        // Checks the data
        if (!provider.get().parse(parameters).isPresent()) {
            return Ack.NOK;
        }
        // Saving
        return buildFilterRepository.save(account.id(), branchId.getValue(), name, type, parameters);
    }

    @Override
    public Ack deleteFilter(ID branchId, String name) {
        return securityService.getAccount()
                .map(account -> buildFilterRepository.delete(account.id(), branchId.getValue(), name))
                .orElse(Ack.NOK);
    }

    private <T> BuildFilter getBuildFilter(ID branchId, BuildFilterProvider<T> provider, JsonNode jsonData) {
        return provider.parse(jsonData)
                .map(data -> provider.filter(branchId, data))
                .orElse(defaultFilter());
    }

    private <T> Optional<? extends BuildFilterProvider<T>> getBuildFilterProviderByType(String type) {
        Optional<? extends BuildFilterProvider<?>> first = buildFilterProviders.stream()
                .filter(provider -> type.equals(provider.getClass().getName()))
                .findFirst();
        //noinspection unchecked
        return (Optional<? extends BuildFilterProvider<T>>) first;
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<BuildFilterResource<T>> loadBuildFilterResource(TBuildFilter t) {
        return getBuildFilterProviderByType(t.getType())
                .flatMap(provider -> loadBuildFilterResource((BuildFilterProvider<T>) provider, t.getBranchId(), t.getName(), t.getData()));
    }

    private <T> Optional<BuildFilterResource<T>> loadBuildFilterResource(BuildFilterProvider<T> provider, int branchId, String name, JsonNode data) {
        return provider.parse(data).map(parsedData ->
                        new BuildFilterResource<>(
                                ID.of(branchId),
                                name,
                                parsedData
                        )
        );
    }

}
