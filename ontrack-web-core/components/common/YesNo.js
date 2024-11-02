import {Typography} from "antd";

export default function YesNo({id, value, strong = false, prefix = ''}) {
    return <Typography.Text id={id} data-testid={id} strong={strong}>
        {prefix}
        {
            value ? "Yes" : "No"
        }
    </Typography.Text>
}