import {Card} from "antd";

export default function PageSection({loading, title, extra, padding = true, children}) {
    const bodyStyle = {}
    if (!padding) {
        bodyStyle.padding = 0
    }
    return (
        <Card
            title={loading ? "Loading..." : title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            className="ot-line"
            extra={extra}
            bodyStyle={bodyStyle}
        >
            {children}
        </Card>
    )
}