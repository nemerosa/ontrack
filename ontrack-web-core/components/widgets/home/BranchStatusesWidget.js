import {useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Space, Table, Typography} from "antd";
import {FaBan} from "react-icons/fa";
import {legacyValidationStampUri, promotionLevelUri} from "@components/common/Links";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import Timestamp from "@components/common/Timestamp";
import PredefinedPromotionLevelImage from "@components/promotionLevels/PredefinedPromotionLevelImage";
import PredefinedValidationStampImage from "@components/validationStamps/PredefinedValidationStampImage";
import {toMilliSeconds} from "@components/common/SelectInterval";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import LegacyLink from "@components/common/LegacyLink";
import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import Link from "next/link";
import BuildLink from "@components/builds/BuildLink";

export default function BranchStatusesWidget({promotions, validations, refreshInterval, branches, title}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)

    const [dataSource, setDataSource] = useState([])
    const [columns, setColumns] = useState([])

    const [refreshCount, setRefreshCount] = useState(0)
    const [refreshIntervalHandler, setRefreshIntervalHandler] = useState(undefined)

    const getBranch = data => {
        if (data.projects) {
            const project = data.projects[0]
            if (project.branches) {
                const branch = project.branches[0]
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

    const refresh = () => {
        setRefreshCount(refreshCount + 1)
    }

    useEffect(() => {
        if (refreshInterval && refreshInterval.count) {
            setRefreshIntervalHandler(setInterval(refresh, toMilliSeconds(refreshInterval)))
        } else {
            if (refreshIntervalHandler) {
                clearInterval(refreshIntervalHandler)
                setRefreshIntervalHandler(undefined)
            }
        }
    }, [refreshInterval])

    if (refreshInterval && refreshInterval.count) {
        setInterval(() => {
            setRefreshCount(refreshCount + 1)
        }, toMilliSeconds(refreshInterval))
    }

    useEffect(() => {
        if (client) {
            setLoading(true)
            const branchLoadings = branches.map(({project, branch}) =>
                client.request(
                    gql`
                        query BranchStatus(
                            $project: String!,
                            $branch: String!,
                            $promotions: [String!]!,
                            $validations: [String!]!,
                        ) {
                            projects(name: $project) {
                                id
                                name
                                branches(name: $branch) {
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
                    `,
                    {project, branch, promotions, validations}
                )
            )

            Promise.all(branchLoadings).then(datas => {

                setDataSource(datas.map(data => {
                    const branch = getBranch(data)
                    return branch ? branch : ({})
                }))

                // List of columns to set
                const columnsList = []
                // Branch column
                columnsList.push({
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
                        columnsList.push({
                            key: promotionName,
                            title: <PredefinedPromotionLevelImage name={promotionName}/>,
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
                        columnsList.push({
                            key: validationName,
                            title: <PredefinedValidationStampImage name={validationName}/>,
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
                                                    />
                                                    <LegacyLink href={legacyValidationStampUri(run.validationStamp)}>
                                                        <Typography.Text type="secondary">[history]</Typography.Text>
                                                    </LegacyLink>
                                                </Space>
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
                // Ok for the columns
                setColumns(columnsList)

            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, promotions, validations, branches, refreshCount])

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
            />
        </>
    )
}