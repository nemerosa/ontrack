import PromotionLevelFrequencyChart from "@components/promotionLevels/PromotionLevelFrequencyChart";
import PromotionChartWidget from "@components/widgets/home/PromotionChartWidget";

export default function PromotionFrequencyChartWidget(props) {
    return (
        <PromotionChartWidget
            prefix="Frequency of"
            chart={PromotionLevelFrequencyChart}
            {...props}
        />
    )
}
