import {Typography} from "antd";

export default function EnvironmentOrder({order}) {
    return (
        <Typography.Text type="secondary"
                         title="Environment order number">{order}</Typography.Text>
    )
}