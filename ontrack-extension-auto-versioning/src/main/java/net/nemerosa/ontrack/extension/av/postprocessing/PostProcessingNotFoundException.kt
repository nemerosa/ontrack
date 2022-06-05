package net.nemerosa.ontrack.extension.av.postprocessing

import net.nemerosa.ontrack.common.BaseException

class PostProcessingNotFoundException(id: String) : BaseException("Cannot find post processing with ID = $id")
