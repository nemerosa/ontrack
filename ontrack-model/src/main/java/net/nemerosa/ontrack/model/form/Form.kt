package net.nemerosa.ontrack.model.form

open class Form {

    private val internalFields: MutableMap<String, Field> = mutableMapOf()

    fun name(): Form = with(
            defaultNameField()
    )

    fun password(): Form = with(
            Password.of("password")
                    .label("Password")
                    .length(40)
                    .validation("Password is required.")
    )

    fun description(): Form = with(
            Memo.of("description")
                    .label("Description")
                    .optional()
                    .length(500)
                    .rows(3)
    )

    fun dateTime(): Form = with(
            DateTime.of("dateTime")
                    .label("Date/time")
    )

    fun url(): Form = with(Url.of())

    fun with(field: Field): Form {
        internalFields[field.name] = field
        return this
    }

    fun with(fields: Iterable<Field>): Form {
        fields.forEach { field -> this.internalFields[field.name] = field }
        return this
    }

    fun getField(name: String): Field? {
        return internalFields[name]
    }

    val fields: Collection<Field> get() = internalFields.values

    fun name(value: String?): Form {
        return fill("name", value)
    }

    fun description(value: String?): Form {
        return fill("description", value)
    }

    fun fill(name: String, value: Any?): Form {
        var field: Field? = internalFields[name]
        if (field != null) {
            field = field.value(value)
            internalFields[name] = field
        } else {
            throw FormFieldNotFoundException(name)
        }
        return this
    }

    fun fill(data: Map<String, *>): Form {
        data.forEach { (name, value) ->
            if (internalFields.containsKey(name)) {
                fill(name, value)
            }
        }
        return this
    }

    fun append(form: Form): Form {
        this.internalFields.putAll(form.internalFields)
        return this
    }

    companion object {

        @JvmStatic
        fun nameAndDescription(): Form {
            return Form.create().name().description()
        }

        @JvmStatic
        fun create(): Form {
            return Form()
        }

        @JvmStatic
        fun defaultNameField(): Text {
            return Text.of("name")
                    .label("Name")
                    .length(40)
                    .regex("[A-Za-z0-9_\\.\\-]+")
                    .validation("Name is required and must contain only alpha-numeric characters, underscores, points or dashes.")
        }
    }
}
