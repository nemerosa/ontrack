package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel;
import net.nemerosa.ontrack.model.structure.Reordering;
import net.nemerosa.ontrack.repository.PredefinedPromotionLevelRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static net.nemerosa.ontrack.model.structure.Entity.isEntityDefined;
import static net.nemerosa.ontrack.model.support.ImageHelper.checkImage;

@Service
@Transactional
public class PredefinedPromotionLevelServiceImpl implements PredefinedPromotionLevelService {

    private final SecurityService securityService;
    private final PredefinedPromotionLevelRepository predefinedPromotionLevelRepository;

    @Autowired
    public PredefinedPromotionLevelServiceImpl(
            SecurityService securityService,
            PredefinedPromotionLevelRepository predefinedPromotionLevelRepository) {
        this.securityService = securityService;
        this.predefinedPromotionLevelRepository = predefinedPromotionLevelRepository;
    }

    @Override
    public List<PredefinedPromotionLevel> getPredefinedPromotionLevels() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return predefinedPromotionLevelRepository.getPredefinedPromotionLevels();
    }

    @Override
    public PredefinedPromotionLevel newPredefinedPromotionLevel(PredefinedPromotionLevel stamp) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        ID id = predefinedPromotionLevelRepository.newPredefinedPromotionLevel(stamp);
        return getPredefinedPromotionLevel(id);
    }

    @Override
    public PredefinedPromotionLevel getPredefinedPromotionLevel(ID id) {
        return predefinedPromotionLevelRepository.getPredefinedPromotionLevel(id);
    }

    @Override
    public Document getPredefinedPromotionLevelImage(ID id) {
        // Checks access
        getPredefinedPromotionLevel(id);
        // Repository access
        return predefinedPromotionLevelRepository.getPredefinedPromotionLevelImage(id);
    }

    @Override
    public Optional<PredefinedPromotionLevel> findPredefinedPromotionLevelByName(String name) {
        return predefinedPromotionLevelRepository.findPredefinedPromotionLevelByName(name);
    }

    @Override
    public void savePredefinedPromotionLevel(PredefinedPromotionLevel stamp) {
        // Validation
        isEntityDefined(stamp, "Predefined promotion level must be defined");
        // Security
        securityService.checkGlobalFunction(GlobalSettings.class);
        // Repository
        predefinedPromotionLevelRepository.savePredefinedPromotionLevel(stamp);
    }

    @Override
    public Ack deletePredefinedPromotionLevel(ID predefinedPromotionLevelId) {
        Validate.isTrue(predefinedPromotionLevelId.isSet(), "Predefined promotion level ID must be set");
        securityService.checkGlobalFunction(GlobalSettings.class);
        return predefinedPromotionLevelRepository.deletePredefinedPromotionLevel(predefinedPromotionLevelId);
    }

    @Override
    public void setPredefinedPromotionLevelImage(ID predefinedPromotionLevelId, Document document) {
        // Checks the image type
        checkImage(document);
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings.class);
        // Repository
        predefinedPromotionLevelRepository.setPredefinedPromotionLevelImage(predefinedPromotionLevelId, document);

    }

    @Override
    public void reorderPromotionLevels(Reordering reordering) {
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings.class);
        // Repository
        predefinedPromotionLevelRepository.reorderPredefinedPromotionLevels(reordering);
    }

}
