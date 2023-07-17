import {Card} from "antd";

export default function PageSection({loading, title, extra, children}) {
    return (
        <Card
            title={loading ? "Loading..." : title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            style={{
                width: '100%'
            }}
            extra={extra}
        >
            {children}
        </Card>
    )
}