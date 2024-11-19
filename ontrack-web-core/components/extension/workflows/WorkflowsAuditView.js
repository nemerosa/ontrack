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
import NotificationRecordLink from "@components/extension/notifications/NotificationRecordLink";

export default function WorkflowsAuditView() {

    const query = gql`
        query WorkflowsInstances(
            $offset: Int!,
            $size: Int!,
        ) {
            workflowInstances(offset: $offset, size: $size) {
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
                            key: 'notification',
                            title: "Notification",
                            render: (_, instance) => {
                                const notificationRecordId = instance.event.values.find(it => it.name === 'notificationRecordId')?.value
                                if (notificationRecordId) {
                                    return <NotificationRecordLink recordId={notificationRecordId}/>
                                } else {
                                    return undefined
                                }
                            }
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