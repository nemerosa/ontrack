import {Badge} from "antd";

export default function LegacyIndicator({children}) {
    return (
        <Badge status="warning" dot title="Link to the legacy UI">
            {children}
        </Badge>
    )
}