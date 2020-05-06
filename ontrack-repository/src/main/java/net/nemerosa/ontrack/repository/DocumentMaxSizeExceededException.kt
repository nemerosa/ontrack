package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.exceptions.InputException

class DocumentMaxSizeExceededException(
        private val maxSize: String,
        private val actualSize: String
) : InputException(
        "Document size cannot exceed $maxSize. Its size was $actualSize"
)