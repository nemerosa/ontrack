package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.ServiceConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class ServiceConfigurator extends AbstractField<ServiceConfigurator> {

    private List<ServiceConfigurationSource> sources = new ArrayList<>();

    protected ServiceConfigurator(String name) {
        super("service-configurator", name);
    }

    public static ServiceConfigurator of(String name) {
        return new ServiceConfigurator(name);
    }

    public ServiceConfigurator sources(List<ServiceConfigurationSource> sources) {
        this.sources = sources;
        return this;
    }
}
