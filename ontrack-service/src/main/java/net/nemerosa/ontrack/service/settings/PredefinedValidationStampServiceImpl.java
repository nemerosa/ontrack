package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.repository.PredefinedValidationStampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PredefinedValidationStampServiceImpl implements PredefinedValidationStampService {

    private final SecurityService securityService;
    private final PredefinedValidationStampRepository predefinedValidationStampRepository;

    @Autowired
    public PredefinedValidationStampServiceImpl(
            SecurityService securityService,
            PredefinedValidationStampRepository predefinedValidationStampRepository) {
        this.securityService = securityService;
        this.predefinedValidationStampRepository = predefinedValidationStampRepository;
    }

    @Override
    public List<PredefinedValidationStamp> getPredefinedValidationStamps() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return predefinedValidationStampRepository.getPredefinedValidationStamps();
    }

    @Override
    public PredefinedValidationStamp newPredefinedValidationStamp(PredefinedValidationStamp stamp) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        ID id = predefinedValidationStampRepository.newPredefinedValidationStamp(stamp);
        return getPredefinedValidationStamp(id);
    }

    @Override
    public PredefinedValidationStamp getPredefinedValidationStamp(ID id) {
        return predefinedValidationStampRepository.getPredefinedValidationStamp(id);
    }

    @Override
    public Optional<PredefinedValidationStamp> findPredefinedValidationStampByName(String name) {
        return predefinedValidationStampRepository.findPredefinedValidationStampByName(name);
    }

}
