import {Typography} from "antd";

export default function YesNo({value, strong = false}) {
    return <Typography.Text strong={strong}>
        {
            value ? "Yes" : "No"
        }
    </Typography.Text>
}