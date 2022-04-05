package net.nemerosa.ontrack.extension.casc.expressions

interface CascExpressionContext {

    val name: String

    fun evaluate(value: String): String

}