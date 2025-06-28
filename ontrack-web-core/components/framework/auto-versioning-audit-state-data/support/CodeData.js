import {Typography} from "antd";

export default function CodeData({text}) {
    return <Typography.Text code copyable>{text}</Typography.Text>
}