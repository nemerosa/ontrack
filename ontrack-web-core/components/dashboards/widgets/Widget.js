import {Card, Skeleton, Space, Typography} from "antd";
import {useContext, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContext";
import WidgetCommand from "@components/dashboards/commands/WidgetCommand";
import {FaRegEdit, FaRegSave, FaWindowClose} from "react-icons/fa";

export function checkContextIn(widget, expectedContexts) {
    return null
    // const dashboard = useContext(DashboardContext)
    // return checkContext(
    //     widget,
    //     expectedContexts.some(it => it === dashboard.context),
    //     "This widget cannot be used in this context."
    // )
}

export function checkContextIs(widget, expectedContext) {
    return null
    // const dashboard = useContext(DashboardContext)
    // return checkContext(
    //     widget,
    //     dashboard.context === expectedContext,
    //     "This widget cannot be used in this context."
    // )
}

export function checkContext(widget, predicate, error) {
    if (predicate) {
        return null
    } else {
        return (
            <Widget title={
                <Space>
                    <Typography.Text type="danger">Error</Typography.Text>
                    {widget}
                </Space>
            }
            >
                {error}
            </Widget>
        )
    }
}

export default function Widget({title, loading, commands, children}) {

    const dashboard = useContext(DashboardContext)

    const [widgetEditionMode, setWidgetEditionMode] = useState(false)

    const editWidget = () => {
        setWidgetEditionMode(true)
    }

    const saveWidgetEdition = () => {
        setWidgetEditionMode(false)
    }

    const cancelWidgetEdition = () => {
        setWidgetEditionMode(false)
    }

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
                    {
                        dashboard.editionMode && <Space size={8}>
                            <WidgetCommand
                                condition={!widgetEditionMode}
                                title="Edit the content of this widget"
                                icon={<FaRegEdit/>}
                                onAction={editWidget}
                            />
                            <WidgetCommand
                                condition={widgetEditionMode}
                                title="Saves the changes for this widget"
                                icon={<FaRegSave/>}
                                onAction={saveWidgetEdition}
                            />
                            <WidgetCommand
                                condition={widgetEditionMode}
                                title="Cancels the changes for this widget"
                                icon={<FaWindowClose/>}
                                onAction={cancelWidgetEdition}
                            />
                        </Space>
                    }
                    {
                        !dashboard.editionMode && commands && <Space size={8}>
                            {
                                commands.map((command, index) => <span key={index}>{command}</span>)
                            }
                        </Space>
                    }
                </>
            }
        >
            {loading && <Skeleton active/>}
            {!loading && children}
        </Card>
    )
}