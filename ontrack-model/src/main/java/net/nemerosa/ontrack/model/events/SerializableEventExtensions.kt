package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

fun SerializableEvent.withBuild(build: Build) =
    withBranch(build.branch)
        .withEntity(build)

fun SerializableEvent.withBranch(branch: Branch) =
    withProject(branch.project)
        .withEntity(branch)

fun SerializableEvent.withProject(project: Project) =
    withEntity(project)

fun SerializableEvent.merge(event: SerializableEvent) = SerializableEvent(
    id = id,
    eventType = eventType,
    signature = signature,
    entities = entities + event.entities,
    extraEntities = extraEntities + event.extraEntities,
    ref = ref ?: event.ref,
    values = values + event.values,
)