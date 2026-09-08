import PromotionLevelLeadTimeChart from "@components/promotionLevels/PromotionLevelLeadTimeChart";
import PromotionChartWidget from "@components/widgets/home/PromotionChartWidget";

export default function PromotionLeadTimeChartWidget(props) {
    return (
        <PromotionChartWidget
            prefix="Lead time to"
            chart={PromotionLevelLeadTimeChart}
            {...props}
        />
    )
}
