package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.graphql.schema.actions.*
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.http.HttpMethod
import kotlin.reflect.KClass

/**
 * Provides a DSL for simple actions.
 */
abstract class SimpleUIActionsProvider<T : Any>(
        override val targetType: KClass<T>,
        private val uriBuilder: URIBuilder
) : UIActionsProvider<T> {

    /**
     * Action which is based on:
     *
     * 1. getting the form through a GET HTTP call
     * 2. sending the data entered into the form into a mutation
     *
     * @param mutation Name of the mutation. It will be used as the [name][UIAction.name] of the action as well.
     * @param description Description for the action. It will be used as the [description][UIActionLink.description] of the form link.
     * @param form Given the object, returned the associated form or `null` if the form is not available.
     * @param check Checks if the mutation is available
     */
    protected fun mutationForm(
            mutation: String,
            description: String,
            form: (T) -> Form?,
            check: (T) -> Boolean
    ) = UIAction(
            mutation,
            description,
            listOf(
                    UIActionLink(
                            UIActionLinks.FORM,
                            description,
                            HttpMethod.GET
                    ) { t -> form(t)?.let { mi -> uriBuilder.build(mi) } }
            ),
            UIActionMutation(
                    mutation,
                    check
            )
    )

    /**
     * Creates an action which depends only on a mutation (like for a deletion).
     */
    protected fun mutationOnly(
            mutation: String,
            description: String,
            check: (T) -> Boolean
    ) = UIAction(
            mutation,
            description,
            emptyList(),
            UIActionMutation(
                    mutation,
                    check
            )
    )

    /**
     * Creates an action for an item which can be downloaded & uploaded (like an image for example).
     */
    protected fun downloadUpload(
            name: String,
            description: String,
            download: (T) -> Document,
            upload: (T) -> Unit
    ) = UIAction<T>(
            name,
            description,
            listOf(
                    UIActionLink(
                            UIActionLinks.DOWNLOAD,
                            "$description (download)",
                            HttpMethod.GET
                    ) { uriBuilder.build(download(it)) },
                    UIActionLink(
                            UIActionLinks.UPLOAD,
                            "$description (upload)",
                            HttpMethod.POST
                    ) { uriBuilder.build(upload(it)) }
            ),
            null
    )

}
