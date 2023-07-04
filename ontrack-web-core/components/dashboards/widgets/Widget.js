import {Card, Skeleton, Space, Typography} from "antd";

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

export default function Widget({title, loading, commands, children}) {
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
                    commands && <Space size={8}>
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