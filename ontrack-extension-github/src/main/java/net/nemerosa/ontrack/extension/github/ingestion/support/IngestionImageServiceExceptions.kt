package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.model.exceptions.InputException

class IngestionImageProtocolUnsupportedException(protocol: String, ref: String) : InputException(
    """Image protocol not supported: $protocol in image ref $ref"""
)

class IngestionImageRefFormatException(ref: String, message: String) : InputException(
    """Image ref format is incorrect. $message: $ref"""
)

class IngestionImagePNGException(ref: String) : InputException(
    """Only PNG images are supported: $ref"""
)

class IngestionImageMissingGitException(ref: String, message: String) : InputException(
    """Project is not configured. $message: $ref"""
)

class IngestionImageNotFoundException(ref: String) : InputException(
    """Image not found: $ref"""
)
