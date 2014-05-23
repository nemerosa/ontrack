package net.nemerosa.ontrack.boot.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.ui.resource.Resource;

import java.net.URI;

@EqualsAndHashCode(callSuper = false)
@Data
public class BuildResource extends Resource<Build> {

    public BuildResource(Build data, URI href) {
        super(data, href);
    }
}
