import {Typography} from "antd";

export default function ChartOptions({interval, period}) {
    return (
        <>
            <Typography.Text>
                {interval}
                &nbsp;/&nbsp;
                {period}
            </Typography.Text>
        </>
    )
}