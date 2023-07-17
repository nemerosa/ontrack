import {Typography} from "antd";

export default function Display({property}) {
    if (property.value.autoCreate) {
        return <Typography.Text strong type="success">Automatic creation of validation stamps.</Typography.Text>
    } else {
        return <Typography.Text type="secondary">No automatic creation of validation stamps.</Typography.Text>
    }
}