package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.structure.SearchProvider;

public interface SearchExtension extends Extension {

    SearchProvider getSearchProvider();

}
