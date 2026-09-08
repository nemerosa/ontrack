import {Space, Typography} from "antd";
import ChartOptions from "@components/charts/ChartOptions";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";

/**
 * Title of the promotion chart widgets: lead time, stability, frequency and TTR.
 *
 * `project`, `branch` and `promotionLevelName` are the plain names the widget was configured with,
 * while `promotionLevel` is the object its own query loaded - only the latter carries the ids the
 * links need. It is `null` until the query answers, and stays so when the promotion level does
 * not exist, which `notFound` says: in both cases the title falls back to the configured names, so
 * that the user always reads what the widget points at (#1694).
 *
 * The name is kept bold inside the link with a plain `<b>`: `Typography.Text` sets its own colour,
 * which would take the link blue away from the one word most likely to be clicked.
 */
export default function PromotionChartTitle({
                                                prefix,
                                                project,
                                                branch,
                                                promotionLevelName,
                                                promotionLevel,
                                                notFound = false,
                                                interval,
                                                period,
                                            }) {
    return (
        <Space size={4}>
            {prefix}
            {
                promotionLevel ?
                    <PromotionLevelLink
                        promotionLevel={promotionLevel}
                        text={<b>{promotionLevel.name}</b>}
                    /> :
                    <Typography.Text strong>{promotionLevelName}</Typography.Text>
            }
            on
            {
                promotionLevel ?
                    <>
                        <BranchLink branch={promotionLevel.branch}/>@<ProjectLink
                        project={promotionLevel.branch.project}/>
                    </> :
                    <>{branch}@{project}</>
            }
            {
                notFound &&
                <Typography.Text type="secondary">(not found)</Typography.Text>
            }
            &nbsp;<ChartOptions interval={interval} period={period}/>
        </Space>
    )
}
