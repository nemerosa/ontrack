import {Space} from "antd";
import {useContext} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import WidgetCommand from "@components/dashboards/commands/WidgetCommand";
import {FaCompressArrowsAlt, FaExpandArrowsAlt, FaRegEdit, FaRegSave, FaTrash, FaWindowClose} from "react-icons/fa";
import {WidgetContext, WidgetDispatchContext, widgetFormSubmit} from "@components/dashboards/widgets/WidgetContext";
import {WidgetExpansionContext} from "@components/dashboards/layouts/WidgetExpansionContext";
import PageSection from "@components/common/PageSection";

export default function Widget({title, loading, commands, form, children}) {

    const {selectedDashboard} = useContext(DashboardContext)
    const selectedDashboardDispatch = useContext(DashboardDispatchContext)
    const widgetContext = useContext(WidgetContext)
    const widgetDispatch = useContext(WidgetDispatchContext)

    const {expansion, toggleExpansion} = useContext(WidgetExpansionContext)

    const toggleExpanded = () => {
        if (toggleExpansion) toggleExpansion(widgetContext.widget.uuid)
    }

    return (
        <PageSection
            loading={loading}
            title={title}
            extra={
                <>
                    {
                        selectedDashboard.editionMode && <Space size={8}>
                            <WidgetCommand
                                condition={!widgetContext.editionMode}
                                title="Edit the content of this widget"
                                icon={<FaRegEdit/>}
                                onAction={() => widgetDispatch({
                                    type: 'edit'
                                })}
                            />
                            <WidgetCommand
                                condition={!widgetContext.editionMode}
                                title="Remove this widget from the dashboard"
                                icon={<FaTrash/>}
                                onAction={() => selectedDashboardDispatch({
                                    type: 'deleteWidget',
                                    widgetUuid: widgetContext.widget.uuid,
                                })}
                            />
                            <WidgetCommand
                                condition={widgetContext.editionMode}
                                title="Saves the changes for this widget"
                                icon={<FaRegSave/>}
                                onAction={() => widgetFormSubmit(widgetContext, widgetDispatch, selectedDashboardDispatch)}
                            />
                            <WidgetCommand
                                condition={widgetContext.editionMode}
                                title="Cancels the changes for this widget"
                                icon={<FaWindowClose/>}
                                onAction={() => widgetDispatch({
                                    type: 'cancel'
                                })}
                            />
                        </Space>
                    }
                    {
                        !selectedDashboard.editionMode && <Space size={8}>
                            {
                                commands && commands.map((command, index) => <span key={index}>{command}</span>)
                            }
                            <WidgetCommand
                                condition={expansion && expansion.uuid !== widgetContext.widget.uuid}
                                title="Makes the widget full size"
                                icon={<FaExpandArrowsAlt/>}
                                onAction={toggleExpanded}
                            />
                            <WidgetCommand
                                condition={expansion && expansion.uuid === widgetContext.widget.uuid}
                                title="Makes the widget to its regular size"
                                icon={<FaCompressArrowsAlt/>}
                                onAction={toggleExpanded}
                            />
                        </Space>
                    }
                </>
            }
        >
            {!widgetContext.editionMode && children}
            {widgetContext.editionMode && form}
        </PageSection>
    )
}