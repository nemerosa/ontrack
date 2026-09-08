import {Space, Typography} from "antd";
import ChartOptions from "@components/charts/ChartOptions";
import ValidationStampLink from "@components/validationStamps/ValidationStampLink";
import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";

/**
 * Title of the validation chart widgets: stability and metrics.
 *
 * The same shape as `PromotionChartTitle`: `project`, `branch` and `validationStampName` are the
 * configured names, `validationStamp` the loaded object carrying the ids the links need, `null`
 * while loading and when the stamp does not exist - which `notFound` says.
 */
export default function ValidationChartTitle({
                                                 prefix,
                                                 project,
                                                 branch,
                                                 validationStampName,
                                                 validationStamp,
                                                 notFound = false,
                                                 interval,
                                                 period,
                                             }) {
    return (
        <Space size={4}>
            {prefix}
            {
                validationStamp ?
                    <ValidationStampLink
                        validationStamp={validationStamp}
                        text={<b>{validationStamp.name}</b>}
                    /> :
                    <Typography.Text strong>{validationStampName}</Typography.Text>
            }
            on
            {
                validationStamp ?
                    <>
                        <BranchLink branch={validationStamp.branch}/>@<ProjectLink
                        project={validationStamp.branch.project}/>
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
