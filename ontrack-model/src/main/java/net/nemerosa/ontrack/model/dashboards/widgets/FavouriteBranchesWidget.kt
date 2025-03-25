package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class FavouriteBranchesWidget : AbstractWidget<FavouriteBranchesWidget.FavouriteBranchesWidgetConfig>(
    key = "home/FavouriteBranches",
    name = "Favourite branches",
    description = "Displays the list of the branches you have selected as favourites. You can display them for one project in particular or for all projects.",
    defaultConfig = FavouriteBranchesWidgetConfig(project = null),
    preferredHeight = 20,
) {
    data class FavouriteBranchesWidgetConfig(
        val project: String?,
    ) : WidgetConfig
}