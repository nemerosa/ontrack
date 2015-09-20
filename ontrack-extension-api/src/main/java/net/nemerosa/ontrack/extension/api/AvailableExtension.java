package net.nemerosa.ontrack.extension.api;

import lombok.Data;
import net.nemerosa.ontrack.model.extension.Extension;

@Data
public class AvailableExtension<X extends Extension> {

    private final X extension;
    private final boolean enabled;

}
