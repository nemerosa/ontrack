import {Typography} from "antd";

export default function Display({property}) {
    return (
        <Typography.Text>{property.value.configuration?.remote}</Typography.Text>
    )
}