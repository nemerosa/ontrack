import {Card, Skeleton, Space, Typography} from "antd";
import WidgetCommand from "@components/dashboards/commands/WidgetCommand";
import {FaPlus, FaRegEdit} from "react-icons/fa";

export function checkContextIn(widget, context, expectedContexts) {
    return checkContext(
        widget,
        expectedContexts.some(it => it === context),
        "This widget cannot be used in this context."
    )
}

export function checkContextIs(widget, context, expectedContext) {
    return checkContext(
        widget,
        context === expectedContext,
        "This widget cannot be used in this context."
    )
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

export default function Widget({title, loading, commands, editionMode, children}) {

    const editWidget = () => {

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
                        editionMode && <Space size={8}>
                            <WidgetCommand
                                condition={true}
                                title="Edit the content of this widget"
                                icon={<FaRegEdit/>}
                                onAction={editWidget}
                            />
                        </Space>
                    }
                    {
                        !editionMode && commands && <Space size={8}>
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