package net.nemerosa.ontrack.common

import org.apache.commons.lang3.exception.ExceptionUtils

private const val MAX_STACK_HEIGHT = 20
private const val MAX_STACK_LENGTH = 2000

fun reducedStackTrace(error: Throwable) =
    ExceptionUtils.getStackFrames(error).take(MAX_STACK_HEIGHT).joinToString("\n").take(MAX_STACK_LENGTH)
