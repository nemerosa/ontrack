import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {usePromotionLevel} from "@components/widgets/home/promotionChartUtils";
import PromotionLevelFrequencyChart from "@components/promotionLevels/PromotionLevelFrequencyChart";
import {Space, Typography} from "antd";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import ChartOptions from "@components/charts/ChartOptions";

export default function PromotionFrequencyChartWidget({project, branch, promotionLevel, interval, period}) {

    const {setTitle} = useContext(DashboardWidgetCellContext)

    const promotionLevelObject = usePromotionLevel(project, branch, promotionLevel)

    useEffect(() => {
        if (promotionLevelObject) {
            setTitle(
                <>
                    <Space size={4}>
                        Frequency of
                        <PromotionLevelImage promotionLevel={promotionLevelObject}/>
                        <Typography.Text strong>{promotionLevel}</Typography.Text>
                        on {branch}@${project}
                        &nbsp;<ChartOptions interval={interval} period={period}/>
                    </Space>
                </>
            )
        }
    }, [promotionLevelObject]);

    return (
        <>
            {
                promotionLevelObject &&
                <PromotionLevelFrequencyChart
                    promotionLevel={promotionLevelObject}
                    interval={interval}
                    period={period}
                />
            }
        </>
    )
}