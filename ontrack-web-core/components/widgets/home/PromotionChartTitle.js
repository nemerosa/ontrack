import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import {Space, Typography} from "antd";
import ChartOptions from "@components/charts/ChartOptions";

export default function PromotionChartTitle({prefix, project, branch, promotionLevel, interval, period}) {
    return (
        <Space size={4}>
            {prefix}
            <PromotionLevelImage promotionLevel={promotionLevel}/>
            <Typography.Text strong>{promotionLevel.name}</Typography.Text>
            on {branch}@{project}
            &nbsp;<ChartOptions interval={interval} period={period}/>
        </Space>
    )
}