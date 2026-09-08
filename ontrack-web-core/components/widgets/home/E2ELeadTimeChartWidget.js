import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {usePromotionLevel} from "@components/widgets/home/promotionChartUtils";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import {Space, Typography} from "antd";
import ChartOptions from "@components/charts/ChartOptions";
import ProjectLink from "@components/projects/ProjectLink";
import BranchLink from "@components/branches/BranchLink";
import E2ELeadTimeChart from "@components/promotionLevels/E2ELeadTimeChart";
import ChartTargetNotFound from "@components/widgets/home/ChartTargetNotFound";
import ChartTargetError from "@components/widgets/home/ChartTargetError";

/**
 * One end of the end-to-end lead time title: `project/branch/PROMOTION`, linked once the promotion
 * level is loaded, spelled from the configured names until then and when it does not exist.
 */
function E2ELeadTimeChartTitleEnd({project, branch, promotionLevel, promotionLevelObject}) {
    return promotionLevelObject ?
        <>
            <ProjectLink project={promotionLevelObject.branch.project}/>/<BranchLink
            branch={promotionLevelObject.branch}/>/<PromotionLevelLink
            promotionLevel={promotionLevelObject}
            text={<b>{promotionLevelObject.name}</b>}/>
        </> :
        <>{project}/{branch}/<Typography.Text strong>{promotionLevel}</Typography.Text></>
}

/**
 * The end-to-end lead time widget builds its own title rather than going through
 * `PromotionChartTitle`: it spans two promotion levels on two different branches. Either end may be
 * missing, in which case the title is flagged as not found and the body names each missing end
 * (#1694).
 */
export default function E2ELeadTimeChartWidget({
                                                   project,
                                                   branch,
                                                   promotionLevel,
                                                   targetProject,
                                                   targetBranch,
                                                   targetPromotionLevel,
                                                   maxDepth = 5,
                                                   interval,
                                                   period
                                               }) {

    const {setTitle} = useContext(DashboardWidgetCellContext)

    const source = usePromotionLevel(project, branch, promotionLevel)
    const target = usePromotionLevel(targetProject, targetBranch, targetPromotionLevel)

    const notFound = source.notFound || target.notFound

    useEffect(() => {
        setTitle(
            <Space size={4}>
                Lead time from
                <E2ELeadTimeChartTitleEnd
                    project={project}
                    branch={branch}
                    promotionLevel={promotionLevel}
                    promotionLevelObject={source.promotionLevelObject}
                />
                to
                <E2ELeadTimeChartTitleEnd
                    project={targetProject}
                    branch={targetBranch}
                    promotionLevel={targetPromotionLevel}
                    promotionLevelObject={target.promotionLevelObject}
                />
                {
                    notFound &&
                    <Typography.Text type="secondary">(not found)</Typography.Text>
                }
                &nbsp;<ChartOptions interval={interval} period={period}/>
            </Space>
        )
    }, [
        setTitle,
        project, branch, promotionLevel, source.promotionLevelObject,
        targetProject, targetBranch, targetPromotionLevel, target.promotionLevelObject,
        notFound, interval, period,
    ]);

    return (
        <>
            {
                source.promotionLevelObject && target.promotionLevelObject &&
                <E2ELeadTimeChart
                    promotionLevel={source.promotionLevelObject}
                    targetPromotionLevel={target.promotionLevelObject}
                    maxDepth={maxDepth}
                    interval={interval}
                    period={period}
                />
            }
            {
                (source.error || target.error) &&
                <ChartTargetError error={source.error || target.error}/>
            }
            {
                source.notFound &&
                <ChartTargetNotFound
                    entity="Promotion level"
                    name={promotionLevel}
                    project={project}
                    branch={branch}
                />
            }
            {
                target.notFound &&
                <ChartTargetNotFound
                    entity="Promotion level"
                    name={targetPromotionLevel}
                    project={targetProject}
                    branch={targetBranch}
                />
            }
        </>
    )
}
