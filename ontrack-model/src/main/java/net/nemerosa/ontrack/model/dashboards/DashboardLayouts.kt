package net.nemerosa.ontrack.model.dashboards

object DashboardLayouts {

    val defaultLayout = DashboardLayout(
        "Default",
        "Default layout",
        "Putting the widgets one under each other, in a single column."
    )

    val main2ChildrenLayout = DashboardLayout(
        "Main2",
        "Main with 2 children columns",
        "First widget takes the whole size, and is followed by two columns."
    )

    val main3ChildrenLayout = DashboardLayout(
        "Main3",
        "Main with 3 children columns",
        "First widget takes the whole size, and is followed by three columns."
    )

    val columns2 = DashboardLayout(
        "Columns2",
        "2 columns",
        "Widgets spread on two columns."
    )

    val layouts = listOf(
        defaultLayout,
        main2ChildrenLayout,
        main3ChildrenLayout,
        columns2,
    )

}