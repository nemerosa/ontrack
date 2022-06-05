package net.nemerosa.ontrack.extension.av.properties

class RegexFilePropertyType(val regex: String) : AbstractRegexFilePropertyType() {

    override fun getPropertyRegex(property: String?): String = regex

    override val id: String
        get() {
            error("Not supposed to be registered as a component")
        }

}