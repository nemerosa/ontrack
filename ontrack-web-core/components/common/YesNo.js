import {Typography} from "antd";

export default function YesNo({value, strong = false, prefix = ''}) {
    return <Typography.Text strong={strong}>
        {prefix}
        {
            value ? "Yes" : "No"
        }
    </Typography.Text>
}