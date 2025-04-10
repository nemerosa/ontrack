import {Space, Typography} from "antd";
import ChartOptions from "@components/charts/ChartOptions";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";

export default function ValidationChartTitle({prefix, project, branch, validationStamp, interval, period}) {
    return (
        <Space size={4}>
            {prefix}
            <ValidationStampImage validationStamp={validationStamp}/>
            <Typography.Text strong>{validationStamp.name}</Typography.Text>
            on {branch}@{project}
            &nbsp;<ChartOptions interval={interval} period={period}/>
        </Space>
    )
}