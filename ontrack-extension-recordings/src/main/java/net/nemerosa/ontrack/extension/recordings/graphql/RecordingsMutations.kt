package net.nemerosa.ontrack.extension.recordings.graphql

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.recordings.Recording
import net.nemerosa.ontrack.extension.recordings.RecordingsCleanupService
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class RecordingsMutations(
        private val extensionManager: ExtensionManager,
        private val recordingsCleanupService: RecordingsCleanupService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation>
        get() = extensionManager.getExtensions(RecordingsExtension::class.java)
                .map { extension ->
                    createPurgeMutation(extension)
                }

    private fun <R : Recording, F : Any> createPurgeMutation(extension: RecordingsExtension<R, F>): Mutation =
            unitNoInputMutation(
                    name = "purge${extension.id.replaceFirstChar { it.uppercase() }}Recordings",
                    description = "Purging all records for ${extension.displayName}"
            ) {
                recordingsCleanupService.purge(extension)
            }
}