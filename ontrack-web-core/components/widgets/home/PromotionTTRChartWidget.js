import PromotionLevelTTRChart from "@components/promotionLevels/PromotionLevelTTRChart";
import PromotionChartWidget from "@components/widgets/home/PromotionChartWidget";

export default function PromotionTTRChartWidget(props) {
    return (
        <PromotionChartWidget
            prefix="TTR to"
            chart={PromotionLevelTTRChart}
            {...props}
        />
    )
}
