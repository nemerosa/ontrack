package net.nemerosa.ontrack.model.templating

interface TemplatingFilter {

    val id: String

    fun apply(text: String): String

}