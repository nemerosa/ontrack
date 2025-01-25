package net.nemerosa.ontrack.json

class JsonParseException : RuntimeException {

    constructor(e: Exception?) : super("Cannot parse JSON", e)

    constructor(message: String?) : super(message)

}
