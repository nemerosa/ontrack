import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import {Space, Typography} from "antd";
import PromotionLevelTTRChart from "@components/promotionLevels/PromotionLevelTTRChart";
import ChartOptions from "@components/charts/ChartOptions";
import {usePromotionLevel} from "@components/widgets/home/promotionChartUtils";

export default function PromotionTTRChartWidget({project, branch, promotionLevel, interval, period}) {

    const {setTitle} = useContext(DashboardWidgetCellContext)

    const promotionLevelObject = usePromotionLevel(project, branch, promotionLevel)

    useEffect(() => {
        if (promotionLevelObject) {
            setTitle(
                <>
                    <Space size={4}>
                        TTR to
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
                <PromotionLevelTTRChart
                    promotionLevel={promotionLevelObject}
                    interval={interval}
                    period={period}
                />
            }
        </>
    )
}