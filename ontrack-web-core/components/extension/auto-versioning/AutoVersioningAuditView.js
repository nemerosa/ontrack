import {Form, Input, Space, Spin, Tooltip, Typography} from "antd";
import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import Link from "next/link";
import {autoVersioningAuditEntryUri} from "@components/common/Links";
import AutoVersioningAuditEntryTarget from "@components/extension/auto-versioning/AutoVersioningAuditEntryTarget";
import ProjectLinkByName from "@components/projects/ProjectLinkByName";
import AutoVersioningApproval from "@components/extension/auto-versioning/AutoVersioningApproval";
import {FaSquare} from "react-icons/fa";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";
import AutoVersioningAuditEntryPR from "@components/extension/auto-versioning/AutoVersioningAuditEntryPR";
import TimestampText from "@components/common/TimestampText";
import Duration from "@components/common/Duration";
import SelectProject from "@components/projects/SelectProject";
import SelectBoolean from "@components/common/SelectBoolean";
import SelectAutoVersioningAuditState from "@components/extension/auto-versioning/SelectAutoVersioningAuditState";
import AutoVersioningSchedule from "@components/extension/auto-versioning/AutoVersioningSchedule";

export default function AutoVersioningAuditView() {
    return (
        <>
            <Space className="ot-line" direction="vertical">
                <StandardTable
                    filterForm={[
                        <Form.Item
                            key="targetProject"
                            name="targetProject"
                            label="Target project"
                        >
                            <SelectProject/>
                        </Form.Item>,
                        <Form.Item
                            key="targetBranch"
                            name="targetBranch"
                            label="Target branch"
                        >
                            <Input/>
                        </Form.Item>,
                        <Form.Item
                            key="sourceProject"
                            name="sourceProject"
                            label="Source project"
                        >
                            <SelectProject/>
                        </Form.Item>,
                        <Form.Item
                            key="version"
                            name="version"
                            label="Version"
                        >
                            <Input/>
                        </Form.Item>,
                        <Form.Item
                            key="running"
                            name="running"
                            label="Running"
                        >
                            <SelectBoolean/>
                        </Form.Item>,
                        <Form.Item
                            key="state"
                            name="state"
                            label="State"
                        >
                            <SelectAutoVersioningAuditState/>
                        </Form.Item>,
                    ]}
                    query={
                        gql`
                            query AutoVersioningAudit(
                                $offset: Int!,
                                $size: Int!,
                                $targetProject: String,
                                $targetBranch: String,
                                $sourceProject: String,
                                $version: String,
                                $state: String,
                                $running: Boolean,
                                $routing: String,
                                $queue: String,
                            ) {
                                autoVersioningAuditEntries(
                                    offset: $offset,
                                    size: $size,
                                    filter: {
                                        project: $targetProject,
                                        branch: $targetBranch,
                                        source: $sourceProject,
                                        version: $version,
                                        state: $state,
                                        running: $running,
                                        routing: $routing,
                                        queue: $queue,
                                    }
                                ) {
                                    pageInfo {
                                        nextPage {
                                            offset
                                            size
                                        }
                                    }
                                    pageItems {
                                        mostRecentState {
                                            creation {
                                                time
                                            }
                                            state
                                            data
                                        }
                                        duration
                                        running
                                        audit {
                                            creation {
                                                time
                                            }
                                            state
                                            data
                                        }
                                        routing
                                        queue
                                        order {
                                            uuid
                                            sourceProject
                                            sourcePromotion
                                            branch {
                                                id
                                                name
                                                project {
                                                    id
                                                    name
                                                }
                                            }
                                            qualifier
                                            repositoryHtmlURL
                                            targetPath
                                            targetRegex
                                            targetProperty
                                            targetPropertyRegex
                                            targetPropertyType
                                            targetVersion
                                            autoApproval
                                            autoApprovalMode
                                            upgradeBranchPattern
                                            postProcessing
                                            postProcessingConfig
                                            validationStamp
                                            schedule
                                        }
                                    }
                                }
                            }
                        `
                    }
                    queryNode="autoVersioningAuditEntries"
                    autoRefresh={true}
                    variables={{}}
                    filter={{}}
                    columns={[
                        {
                            key: 'uuid',
                            title: 'UUID',
                            render: (_, entry) => <Tooltip
                                title="Displays the details of this entry into a separate page.">
                                <Link href={autoVersioningAuditEntryUri(entry.order.uuid)}>{entry.order.uuid}</Link>
                            </Tooltip>
                        },
                        {
                            key: 'target',
                            title: 'Target',
                            render: (_, entry) => <AutoVersioningAuditEntryTarget entry={entry}/>
                        },
                        {
                            key: 'source',
                            title: "Source project",
                            render: (_, entry) => <ProjectLinkByName name={entry.order.sourceProject}/>
                        },
                        {
                            key: 'promotion',
                            title: "Promotion",
                            render: (_, entry) => <Typography.Text>{entry.order.sourcePromotion}</Typography.Text>,
                        },
                        {
                            key: 'qualifier',
                            title: "Qualifier",
                            render: (_, entry) => <Typography.Text>{entry.order.qualifier}</Typography.Text>,
                        },
                        {
                            key: 'version',
                            title: "Version",
                            render: (_, entry) => <Typography.Text>{entry.order.targetVersion}</Typography.Text>,
                        },
                        {
                            key: 'post-processing',
                            title: "Post-processing",
                            render: (_, entry) => <>
                                {
                                    entry.order.postProcessing &&
                                    <Typography.Text>{entry.order.postProcessing}</Typography.Text>
                                }
                                {
                                    !entry.order.postProcessing &&
                                    <Typography.Text type="secondary" italic>None</Typography.Text>
                                }
                            </>,
                        },
                        {
                            key: 'approval',
                            title: "Approval",
                            render: (_, entry) => <AutoVersioningApproval
                                autoApproval={entry.order.autoApproval}
                                autoApprovalMode={entry.order.autoApprovalMode}
                            />,
                        },
                        {
                            key: 'schedule',
                            title: "Schedule",
                            render: (_, entry) => <AutoVersioningSchedule schedule={entry.order.schedule}/>,
                        },
                        {
                            key: 'running',
                            title: "Running",
                            render: (_, entry) =>
                                <>
                                    {
                                        entry.running &&
                                        <Space>
                                            <Spin size="small"/>
                                            Running
                                        </Space>
                                    }
                                    {
                                        !entry.running &&
                                        <Typography.Text disabled>
                                            <Space>
                                                <FaSquare/>
                                                Finished
                                            </Space>
                                        </Typography.Text>
                                    }
                                </>,
                        },
                        {
                            key: 'state',
                            title: "State",
                            render: (_, entry) => <AutoVersioningAuditEntryState status={entry.mostRecentState}/>,
                        },
                        {
                            key: 'pr',
                            title: "PR",
                            render: (_, entry) => <AutoVersioningAuditEntryPR entry={entry}/>,
                        },
                        {
                            key: 'timestamp',
                            title: "Timestamp",
                            render: (_, entry) => <TimestampText
                                value={entry.mostRecentState.creation.time}
                                format="YYYY MMM DD, HH:mm:ss"
                            />,
                        },
                        {
                            key: 'duration',
                            title: "Duration",
                            render: (_, entry) =>
                                <Duration
                                    displaySeconds={true}
                                    seconds={Math.floor(entry.duration / 1000)}
                                />,
                        }
                    ]}
                />
            </Space>
        </>
    )
}