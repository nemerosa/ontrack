package net.nemerosa.ontrack.model.labels

fun LabelManagementService.findLabelByDisplay(display: String): Label? {
    val (category, name) = Label.categoryAndNameFromDisplay(display)
    val labels = findLabels(category, name)
    return labels.firstOrNull()
}
