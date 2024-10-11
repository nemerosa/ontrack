package net.nemerosa.ontrack.model.form

import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyLabel
import java.time.LocalDateTime
import kotlin.reflect.KProperty1

fun <T> Form.textField(
    property: KProperty1<T, String?>,
    value: String?,
    readOnly: Boolean = false,
): Form =
    with(
        Text.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .readOnly(readOnly)
            .value(value)
    )

fun <T> Form.memoField(
    property: KProperty1<T, String?>,
    value: String?,
    readOnly: Boolean = false,
): Form =
    with(
        Memo.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .readOnly(readOnly)
            .value(value)
    )

fun <T> Form.passwordField(property: KProperty1<T, String?>): Form =
    with(
        Password.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
    )

fun <T> Form.urlField(property: KProperty1<T, String?>, value: String?): Form =
    with(
        Url.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .value(value)
    )

fun <T> Form.yesNoField(property: KProperty1<T, Boolean?>, value: Boolean?): Form =
    with(
        YesNo.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .value(value)
    )

fun <T> Form.intField(
    property: KProperty1<T, kotlin.Int?>,
    value: kotlin.Int?,
    min: kotlin.Int? = null,
    max: kotlin.Int? = null,
    step: kotlin.Int? = null,
): Form =
    with(
        Int.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .value(value)
            .apply {
                if (min != null) min(min)
                if (max != null) max(max)
                if (step != null) step(step)
            }
    )

fun <T> Form.longField(property: KProperty1<T, Long>, value: Long?): Form =
    with(
        Int.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .value(value)
    )

fun Form.selectionOfString(
    property: KProperty1<*, String?>,
    items: List<String>,
    value: String?,
): Form =
    with(
        Selection.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .items(items.map { IdName(it, it) })
            .value(value)
    )

/**
 * Multiple strings
 */
fun Form.multiStrings(
    property: KProperty1<*, List<String>?>,
    value: List<String>?,
): Form =
    with(
        MultiStrings.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .value(value)
    )

/**
 * Date/time field
 */
fun Form.dateTime(property: KProperty1<*, LocalDateTime?>, value: LocalDateTime?): Form =
    with(
        DateTime.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .value(value)
    )

/**
 * Multiform field
 */
fun <T> Form.multiform(
    property: KProperty1<*, List<T>>,
    items: List<T>?,
    formCreation: () -> Form,
): Form = with(
    MultiForm.of(property.name, formCreation())
        .label(getPropertyLabel(property))
        .help(getPropertyDescription(property))
        .value(items ?: emptyList<T>())
)

inline fun <reified E : Enum<E>> Form.enumField(
    property: KProperty1<*, E?>,
    value: E?,
): Form =
    with(

        Selection.of(property.name)
            .label(getPropertyLabel(property))
            .help(getPropertyDescription(property))
            .optional(property.returnType.isMarkedNullable)
            .items(
                enumValues<E>().map { e ->
                    IdName(id = e.name, name = e.name)
                }
            )
            .value(value?.name)
    )
