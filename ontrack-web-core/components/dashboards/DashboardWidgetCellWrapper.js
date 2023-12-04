import GridCell from "@components/grid/GridCell";
import {Dynamic} from "@components/common/Dynamic";
import {useContext, useState} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {FaCompressArrowsAlt, FaExpandArrowsAlt, FaPencilAlt, FaRegSave, FaTrash, FaWindowClose} from "react-icons/fa";

import GridCellCommand from "@components/grid/GridCellCommand";

export default function DashboardWidgetCellWrapper({widget}) {

    const {
        title,
        extra,
        expanded,
        toggleExpansion,
        dashboardEdition,
        widgetEdition,
        widgetSaving,
        startWidgetEdition,
        saveWidgetEdition,
        cancelWidgetEdition,
        deleteWidget,
    } = useContext(DashboardWidgetCellContext)

    return (
        <>
            <GridCell
                title={title}
                isDraggable={dashboardEdition}
                extra={
                    <>
                        {!dashboardEdition && extra}
                        <GridCellCommand
                            condition={!dashboardEdition && !expanded}
                            title="Makes the widget full size"
                            icon={<FaExpandArrowsAlt/>}
                            onAction={toggleExpansion}
                        />
                        <GridCellCommand
                            condition={!dashboardEdition && expanded}
                            title="Makes the widget to its regular size"
                            icon={<FaCompressArrowsAlt/>}
                            onAction={toggleExpansion}
                        />
                        {/* Dashboard edition */}
                        {
                            dashboardEdition &&
                            <>
                                <GridCellCommand
                                    condition={!widgetEdition}
                                    title="Configure this widget"
                                    icon={<FaPencilAlt/>}
                                    onAction={startWidgetEdition}
                                />
                                <GridCellCommand
                                    condition={widgetEdition}
                                    disabled={widgetSaving}
                                    title="Saves the changes for this widget"
                                    icon={<FaRegSave/>}
                                    onAction={saveWidgetEdition}
                                />
                                <GridCellCommand
                                    condition={widgetEdition}
                                    disabled={widgetSaving}
                                    title="Cancels the changes for this widget"
                                    icon={<FaWindowClose/>}
                                    onAction={cancelWidgetEdition}
                                />
                                <GridCellCommand
                                    condition={!widgetEdition}
                                    title="Remove this widget from the dashboard"
                                    icon={<FaTrash/>}
                                    onAction={deleteWidget}
                                />
                            </>
                        }
                    </>
                }
            >
                {
                    !widgetEdition &&
                    <Dynamic
                        path={`widgets/${widget.key}Widget`}
                        props={widget.config}
                    />
                }
                {
                    widgetEdition &&
                    <Dynamic
                        path={`widgets/${widget.key}WidgetForm`}
                        props={widget.config}
                    />
                }
            </GridCell>
        </>
    )
}