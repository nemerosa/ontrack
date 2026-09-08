import ValidationStampMetricsChart from "@components/validationStamps/ValidationStampMetricsChart";
import ValidationChartWidget from "@components/widgets/home/ValidationChartWidget";

export default function ValidationMetricsChartWidget(props) {
    return (
        <ValidationChartWidget
            prefix="Metrics of"
            chart={ValidationStampMetricsChart}
            {...props}
        />
    )
}
