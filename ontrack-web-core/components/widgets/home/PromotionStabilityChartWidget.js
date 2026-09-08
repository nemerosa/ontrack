import PromotionLevelStabilityChart from "@components/promotionLevels/PromotionLevelStabilityChart";
import PromotionChartWidget from "@components/widgets/home/PromotionChartWidget";

export default function PromotionStabilityChartWidget(props) {
    return (
        <PromotionChartWidget
            prefix="Stability of"
            chart={PromotionLevelStabilityChart}
            {...props}
        />
    )
}
