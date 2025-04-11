import ValidationChartWidgetForm from "@components/widgets/home/ValidationChartWidgetForm";

export default function ValidationFrequencyChartWidgetForm({project, branch, validationStamp, interval, period}) {
    return <ValidationChartWidgetForm
        project={project}
        branch={branch}
        validationStamp={validationStamp}
        interval={interval}
        period={period}
    />
}