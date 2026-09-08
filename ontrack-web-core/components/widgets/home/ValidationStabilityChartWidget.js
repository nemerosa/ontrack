import ValidationStampStabilityChart from "@components/validationStamps/ValidationStampStabilityChart";
import ValidationChartWidget from "@components/widgets/home/ValidationChartWidget";

export default function ValidationStabilityChartWidget(props) {
    return (
        <ValidationChartWidget
            prefix="Stability of"
            chart={ValidationStampStabilityChart}
            {...props}
        />
    )
}
