import {Card, Skeleton, Space, Typography} from "antd";

export function checkContextIs(widget, context, expectedContext) {
    if (context === expectedContext) {
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
                This widget cannot be used in this context.
            </Widget>
        )
    }
}

export default function Widget({title, loading, children}) {
    return (
        <Card
            title={loading ? "Loading..." : title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            style={{
                width: '100%'
            }}
        >
            {loading && <Skeleton active/>}
            {!loading && children}
        </Card>
    )
}