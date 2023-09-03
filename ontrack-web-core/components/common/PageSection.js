import {Card} from "antd";

export default function PageSection({loading, title, extra, padding = true, fullHeight = false, children}) {
    const bodyStyle = {}
    if (!padding) {
        bodyStyle.padding = 0
    }
    const style = {}
    if (fullHeight) {
        style.height = '100%'
    }
    return (
        <Card
            style={style}
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