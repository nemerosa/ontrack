package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.repository.PredefinedValidationStampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

}
