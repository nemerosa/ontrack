package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class FavouriteProjectsWidget : AbstractWidget<FavouriteProjectsWidget.FavouriteProjectsWidgetConfig>(
    key = "home/FavouriteProjects",
    name = "Favourite projects",
    description = "Displays the list of the projects you have selected as favourites.",
    defaultConfig = FavouriteProjectsWidgetConfig.INSTANCE,
    preferredHeight = 4,
) {
    class FavouriteProjectsWidgetConfig : WidgetConfig {
        companion object {
            val INSTANCE = FavouriteProjectsWidgetConfig()
        }
    }
}