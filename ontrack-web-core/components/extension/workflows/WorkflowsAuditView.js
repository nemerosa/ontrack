import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";
import Link from "next/link";
import TriggerLink from "@components/framework/trigger/TriggerLink";
import {Form, Input} from "antd";
import SelectWorkflowInstanceStatus from "@components/extension/workflows/SelectWorkflowInstanceStatus";
import SelectTrigger from "@components/core/model/triggers/SelectTrigger";

export default function WorkflowsAuditView() {

    const query = gql`
        query WorkflowsInstances(
            $offset: Int!,
            $size: Int!,
            $id: String,
            $name: String,
            $status: WorkflowInstanceStatus,
            $triggerId: String,
            $triggerData: String,
        ) {
            workflowInstances(
                offset: $offset,
                size: $size,
                id: $id,
                name: $name,
                status: $status,
                triggerId: $triggerId,
                triggerData: $triggerData,
            ) {
                pageInfo {
                    nextPage {
                        offset
                        size
                    }
                }
                pageItems {
                    key: id
                    id
                    timestamp
                    status
                    finished
                    startTime
                    endTime
                    durationMs
                    event {
                        values {
                            name
                            value
                        }
                    }
                    triggerData {
                        id
                        data
                    }
                    workflow {
                        name
                    }
                }
            }
        }
    `

    return (
        <>
            <Head>
                {pageTitle("Workflows audit")}
            </Head>
            <MainPage
                title="Workflows audit"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="home" href={homeUri()}/>
                ]}
            >
                <StandardTable
                    query={query}
                    queryNode="workflowInstances"
                    size={10}
                    filter={{}}
                    filterForm={[
                        <Form.Item
                            key="id"
                            name="id"
                            label="ID"
                        >
                            <Input style={{width: '16em'}}/>
                        </Form.Item>,
                        <Form.Item
                            key="name"
                            name="name"
                            label="Name"
                        >
                            <Input style={{width: '16em'}}/>
                        </Form.Item>,
                        <Form.Item
                            key="status"
                            name="status"
                            label="Status"
                        >
                            <SelectWorkflowInstanceStatus/>
                        </Form.Item>,
                        <Form.Item
                            key="triggerId"
                            name="triggerId"
                            label="Trigger"
                        >
                            <SelectTrigger/>
                        </Form.Item>,
                        <Form.Item
                            key="triggerData"
                            name="triggerData"
                            label="Trigger data"
                        >
                            <Input style={{width: '16em'}}/>
                        </Form.Item>,
                    ]}
                    columns={[
                        {
                            key: 'id',
                            title: 'ID',
                            render: (_, instance) => <Link
                                href={`/extension/workflows/instances/${instance.id}`}>{instance.id}</Link>,
                        },
                        {
                            key: 'name',
                            title: 'Workflow name',
                            render: (_, instance) => instance.workflow.name,
                        },
                        {
                            key: 'trigger',
                            title: "Trigger",
                            render: (_, instance) => <>
                                {
                                    instance.triggerData &&
                                    <TriggerLink triggerData={instance.triggerData}/>
                                }
                            </>
                        },
                        {
                            key: 'status',
                            title: 'Status',
                            render: (_, instance) => <WorkflowInstanceStatus status={instance.status}/>,
                        },
                        {
                            key: 'startTime',
                            title: 'Start time',
                            render: (_, instance) => <TimestampText value={instance.startTime}
                                                                    format="YYYY MMM DD, HH:mm:ss"/>,
                        },
                        {
                            key: 'duration',
                            title: 'Duration',
                            render: (_, instance) => <DurationMs ms={instance.durationMs}/>,
                        },
                        {
                            key: 'timestamp',
                            title: 'Last update',
                            render: (_, instance) => <TimestampText value={instance.timestamp}
                                                                    format="YYYY MMM DD, HH:mm:ss"/>,
                        },
                    ]}
                />
            </MainPage>
        </>
    )
}