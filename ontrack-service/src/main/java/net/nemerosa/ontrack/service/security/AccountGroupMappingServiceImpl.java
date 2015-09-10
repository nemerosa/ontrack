package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import net.nemerosa.ontrack.repository.AccountGroupMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AccountGroupMappingServiceImpl implements AccountGroupMappingService {

    private final AccountGroupMappingRepository accountGroupMappingRepository;

    @Autowired
    public AccountGroupMappingServiceImpl(AccountGroupMappingRepository accountGroupMappingRepository) {
        this.accountGroupMappingRepository = accountGroupMappingRepository;
    }

    @Override
    public Collection<AccountGroup> getGroups(String mapping, String mappedName) {
        return accountGroupMappingRepository.getGroups(mapping, mappedName);
    }
}
