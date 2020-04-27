package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingWrongTypeException;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.AccountGroupMappingRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
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

    @Override
    public List<AccountGroupMapping> getMappings(String mapping) {
        return accountGroupMappingRepository.getMappings(mapping);
    }

    @Override
    public AccountGroupMapping newMapping(String mapping, AccountGroupMappingInput input) {
        return accountGroupMappingRepository.newMapping(mapping, input);
    }

    @Override
    public AccountGroupMapping getMapping(String mapping, ID id) {
        AccountGroupMapping o = accountGroupMappingRepository.getMapping(id);
        if (StringUtils.equals(mapping, o.getType())) {
            return o;
        } else {
            throw new AccountGroupMappingWrongTypeException(mapping, o.getType());
        }
    }

    @Override
    public AccountGroupMapping updateMapping(String mapping, ID id, AccountGroupMappingInput input) {
        getMapping(mapping, id);
        return accountGroupMappingRepository.updateMapping(id, input);
    }

    @Override
    public Ack deleteMapping(String mapping, ID id) {
        getMapping(mapping, id);
        return accountGroupMappingRepository.deleteMapping(id);
    }

    @Override
    public List<AccountGroupMapping> getMappingsForGroup(AccountGroup group) {
        return accountGroupMappingRepository.getMappingsForGroup(group);
    }
}
