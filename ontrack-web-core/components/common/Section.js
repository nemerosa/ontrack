import {Card} from "antd";

export default function Section({title, extra, children}) {
    return (
        <Card
            title={title}
            className="ot-line"
            extra={extra}
        >
            {children}
        </Card>
    )
}