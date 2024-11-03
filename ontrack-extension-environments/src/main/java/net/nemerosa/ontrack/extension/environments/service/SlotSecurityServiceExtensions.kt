package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.security.EnvironmentList
import net.nemerosa.ontrack.extension.environments.security.SlotView
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService

inline fun <reified P : ProjectFunction> SecurityService.isSlotAccessible(slot: Slot): Boolean {
    return isGlobalFunctionGranted(EnvironmentList::class.java) &&
            isProjectFunctionGranted(slot.project, ProjectView::class.java) &&
            isProjectFunctionGranted(slot.project, SlotView::class.java) &&
            (P::class == SlotView::class || isProjectFunctionGranted(slot.project, P::class.java))
}

inline fun <reified P : ProjectFunction> SecurityService.checkSlotAccess(slot: Slot) {
    checkGlobalFunction(EnvironmentList::class.java)
    checkProjectFunction(slot.project, ProjectView::class.java)
    checkProjectFunction(slot.project, SlotView::class.java)
    if (P::class != SlotView::class) {
        checkProjectFunction(slot.project, P::class.java)
    }
}