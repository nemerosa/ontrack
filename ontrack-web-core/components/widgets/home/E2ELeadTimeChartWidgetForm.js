import E2EChartWidgetForm from "@components/widgets/home/E2EChartWidgetForm";

export default function E2ELeadTimeChartWidgetForm({
                                                       project,
                                                       branch,
                                                       promotionLevel,
                                                       targetProject,
                                                       targetBranch,
                                                       targetPromotionLevel,
                                                       maxDepth,
                                                       interval,
                                                       period
                                                   }) {
    return <E2EChartWidgetForm project={project} branch={branch} promotionLevel={promotionLevel}
                               targetProject={targetProject} targetBranch={targetBranch}
                               targetPromotionLevel={targetPromotionLevel}
                               maxDepth={maxDepth}
                               interval={interval} period={period}/>
}