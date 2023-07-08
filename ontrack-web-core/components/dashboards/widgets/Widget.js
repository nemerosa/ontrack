import {Card, Skeleton, Space, Typography} from "antd";
import {useContext, useState} from "react";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import WidgetCommand from "@components/dashboards/commands/WidgetCommand";
import {FaRegEdit, FaRegSave, FaWindowClose} from "react-icons/fa";
import {WidgetContext, WidgetDispatchContext, widgetFormSubmit} from "@components/dashboards/widgets/WidgetContext";

export default function Widget({title, loading, commands, form, children}) {

    const {selectedDashboard} = useContext(DashboardContext)
    // const dashboardDispatch = useContext(DashboardDispatchContext)
    const widgetContext = useContext(WidgetContext)
    const widgetDispatch = useContext(WidgetDispatchContext)

    return (
        <Card
            title={loading ? "Loading..." : title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            style={{
                width: '100%'
            }}
            extra={
                <>
                    {/*{*/}
                    {/*    selectedDashboard.editionMode && <Space size={8}>*/}
                    {/*        <WidgetCommand*/}
                    {/*            condition={!widgetContext.editionMode}*/}
                    {/*            title="Edit the content of this widget"*/}
                    {/*            icon={<FaRegEdit/>}*/}
                    {/*            onAction={() => widgetDispatch({*/}
                    {/*                type: 'edit'*/}
                    {/*            })}*/}
                    {/*        />*/}
                    {/*        <WidgetCommand*/}
                    {/*            condition={widgetContext.editionMode}*/}
                    {/*            title="Saves the changes for this widget"*/}
                    {/*            icon={<FaRegSave/>}*/}
                    {/*            onAction={() => widgetFormSubmit(widgetContext, widgetDispatch, dashboardDispatch)}*/}
                    {/*        />*/}
                    {/*        <WidgetCommand*/}
                    {/*            condition={widgetContext.editionMode}*/}
                    {/*            title="Cancels the changes for this widget"*/}
                    {/*            icon={<FaWindowClose/>}*/}
                    {/*            onAction={() => widgetDispatch({*/}
                    {/*                type: 'cancel'*/}
                    {/*            })}*/}
                    {/*        />*/}
                    {/*    </Space>*/}
                    {/*}*/}
                    {
                        !selectedDashboard.editionMode && commands && <Space size={8}>
                            {
                                commands.map((command, index) => <span key={index}>{command}</span>)
                            }
                        </Space>
                    }
                </>
            }
        >
            {loading && <Skeleton active/>}
            {!loading && !widgetContext.editionMode && children}
            {!loading && widgetContext.editionMode && form}
        </Card>
    )
}