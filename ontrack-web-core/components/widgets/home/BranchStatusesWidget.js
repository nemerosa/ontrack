import {useQueries} from "@components/services/GraphQL";
import {gql} from "graphql-request";
import {Space, Table, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import BranchLink from "@components/branches/BranchLink";
import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import PredefinedPromotionLevelImageByName from "@components/promotionLevels/PredefinedPromotionLevelImageByName";
import BuildLink from "@components/builds/BuildLink";
import Link from "next/link";
import {promotionLevelUri, validationRunUri, validationStampUri} from "@components/common/Links";
import Timestamp from "@components/common/Timestamp";
import {FaBan} from "react-icons/fa";
import PredefinedValidationStampImageByName from "@components/validationStamps/PredefinedValidationStampImageByName";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import ValidationRunData from "@components/framework/validation-run-data/ValidationRunData";
import RunInfo from "@components/common/RunInfo";

export default function BranchStatusesWidget({
                                                 promotionConfigs,
                                                 validationConfigs,
                                                 displayValidationResults,
                                                 displayValidationRun,
                                                 branches = [],
                                                 title
                                             }) {

    const promotions = promotionConfigs ? promotionConfigs.map(it => it.promotionLevel) : []
    const validations = validationConfigs ? validationConfigs.map(it => it.validationStamp) : []

    const query = gql`
        query BranchStatus(
            $project: String!,
            $branch: String!,
            $promotions: [String!]!,
            $validations: [String!]!,
        ) {
            projects(name: $project) {
                id
                name
                branch(name: $branch) {
                    id
                    name
                    promotionStatuses(names: $promotions) {
                        promotionLevel {
                            id
                            name
                        }
                        creation {
                            time
                        }
                        build {
                            id
                            name
                            releaseProperty {
                                value
                            }
                        }
                    }
                    validationStatuses(names: $validations) {
                        id
                        validationStamp {
                            id
                            name
                        }
                        lastStatus {
                            creation {
                                time
                            }
                            statusID {
                                id
                                name
                            }
                        }
                        runInfo {
                            runTime
                            sourceType
                            sourceUri
                            triggerType
                            triggerData
                        }
                        data {
                            descriptor {
                                feature {
                                    id
                                }
                                id
                            }
                            data
                        }
                        build {
                            id
                            name
                            releaseProperty {
                                value
                            }
                        }
                    }
                }
            }
        }
    `

    const queries = branches.map(({project, branch}) => ({
        query,
        variables: {project, branch, promotions, validations}
    }))

    const {data: results, loading} = useQueries(queries, {
        deps: []
    })

    const getBranch = data => {
        if (data && data.projects && data.projects.length > 0) {
            const project = data.projects[0]
            if (project.branch) {
                const branch = project.branch
                return {
                    ...branch,
                    project: {
                        id: project.id,
                        name: project.name,
                    },
                    key: branch.id,
                }
            }
        }
        return undefined
    }

    const dataSource = results ? results.map(data => {
        const branch = getBranch(data)
        return branch ? branch : ({})
    }) : []

    // List of columns to set
    const columns = []
    // Branch column
    columns.push({
        key: 'branch',
        title: "Branch",
        render: (_, branch) => {
            return <Space>
                <ProjectLink project={branch.project}/>
                <span>/</span>
                <BranchLink branch={branch}/>
            </Space>
        }
    })
    // Column per promotion
    if (promotions) {
        promotions.forEach(promotionName => {
            columns.push({
                key: promotionName,
                title: <PredefinedPromotionLevelImageByName name={promotionName} generateIfMissing={true}/>,
                render: (_, branch) => {
                    if (branch.promotionStatuses) {
                        const run = branch.promotionStatuses.find(it => it.promotionLevel.name === promotionName)
                        if (run) {
                            return <Space direction="vertical">
                                {
                                    <Space size={8}>
                                        <BuildLink
                                            build={run.build}
                                        />
                                        <Link href={promotionLevelUri(run.promotionLevel)}>
                                            <Typography.Text type="secondary">[history]</Typography.Text>
                                        </Link>
                                    </Space>
                                }
                                <Timestamp value={run.creation.time}/>
                            </Space>
                        }
                    }
                    return <FaBan/>
                }
            })
        })
    }
    // Column per validation
    if (validations) {
        validations.forEach(validationName => {
            columns.push({
                key: validationName,
                title: <PredefinedValidationStampImageByName name={validationName}/>,
                render: (_, branch) => {
                    if (branch.validationStatuses) {
                        const run = branch.validationStatuses.find(it => it.validationStamp.name === validationName)
                        if (run) {
                            return <Space direction="vertical">
                                {
                                    <Space size={8}>
                                        <ValidationRunStatus
                                            status={run.lastStatus}
                                            tooltip={true}
                                            text={
                                                <BuildLink
                                                    build={run.build}
                                                />
                                            }
                                            href={validationRunUri(run)}
                                        />
                                        <Link href={validationStampUri(run.validationStamp)}>
                                            <Typography.Text type="secondary">[history]</Typography.Text>
                                        </Link>
                                    </Space>
                                }
                                {
                                    displayValidationResults && run.data &&
                                    <ValidationRunData data={run.data}/>
                                }
                                {
                                    displayValidationRun && run.runInfo &&
                                    <RunInfo info={run.runInfo} mode="minimal"/>
                                }
                                <Timestamp value={run.lastStatus.creation.time}/>
                            </Space>
                        }
                    }
                    return <FaBan/>
                }
            })
        })
    }

    const {setTitle} = useContext(DashboardWidgetCellContext)
    useEffect(() => {
        setTitle(title ?? "Branch statuses")
    }, [title])

    return (
        <>
            <Table
                loading={loading}
                dataSource={dataSource}
                columns={columns}
                pagination={false}
                size="small"
            />
        </>
    )
}