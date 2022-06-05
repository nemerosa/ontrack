package net.nemerosa.ontrack.extension.av.postprocessing

import net.nemerosa.ontrack.model.exceptions.InputException

class PostProcessingConfigProvidedWithoutIDException :
    InputException("A post processing configuration is provided, but there is no post processing ID")
