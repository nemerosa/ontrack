import {Card} from "antd";

export default function PageSection({loading, title, extra, children}) {
    return (
        <Card
            title={loading ? "Loading..." : title}
            headStyle={{
                backgroundColor: 'lightgrey'
            }}
            className="ot-line"
            extra={extra}
        >
            {children}
        </Card>
    )
}