import PromotionChartWidgetForm from "@components/widgets/home/PromotionChartWidgetForm";

export default function PromotionTTRChartWidgetForm({project, branch, promotionLevel, interval, period}) {
    return <PromotionChartWidgetForm project={project} branch={branch} promotionLevel={promotionLevel} interval={interval} period={period}/>
}