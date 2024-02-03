package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingFailureException

class MockPostProcessingFailureException(
    message: String,
    override val link: String,
) : RuntimeException(message), PostProcessingFailureException
