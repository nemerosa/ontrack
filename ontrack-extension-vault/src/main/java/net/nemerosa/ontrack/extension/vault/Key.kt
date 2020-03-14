package net.nemerosa.ontrack.extension.vault

import java.beans.ConstructorProperties

data class Key
@ConstructorProperties("payload")
constructor(
        val payload: ByteArray
)