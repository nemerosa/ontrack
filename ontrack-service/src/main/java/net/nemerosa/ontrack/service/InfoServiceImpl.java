package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.Info;
import net.nemerosa.ontrack.model.structure.InfoService;
import net.nemerosa.ontrack.model.support.EnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoServiceImpl implements InfoService {

    private final EnvService envService;

    @Autowired
    public InfoServiceImpl(EnvService envService) {
        this.envService = envService;
    }

    @Override
    public Info getInfo() {
        return new Info(
                envService.getVersion()
        );
    }
}
