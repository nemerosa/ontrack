package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ConfidentialStoreServiceImpl implements ConfidentialStoreService {

    private final Logger logger = LoggerFactory.getLogger(ConfidentialStoreService.class);

    private final ConfidentialStore confidentialStore;

    @Autowired
    public ConfidentialStoreServiceImpl(
            OntrackConfigProperties configProperties,
            Collection<ConfidentialStore> confidentialStores
    ) {
        logger.debug("[keystore] Selection of the key store");
        logger.debug("[keystore] Candidates:");
        confidentialStores.forEach(store ->
                logger.debug("[keystore]    {} --> {}", store.getId(), store.getClass().getName())
        );
        this.confidentialStore = confidentialStores.stream()
                .filter(store ->
                        StringUtils.equals(
                                configProperties.getKeyStore(),
                                store.getId()
                        )
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find key store with ID " + configProperties.getKeyStore()));
        logger.debug("[info] Selected key store: {}", this.confidentialStore.getId());
    }

    @Override
    public ConfidentialStore getConfidentialStore() {
        return confidentialStore;
    }

}
