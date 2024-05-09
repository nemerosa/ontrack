import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import MainPage from "@components/layouts/MainPage";
import Link from "next/link";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Descriptions, Skeleton, Space} from "antd";
import {gql} from "graphql-request";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import WorkflowInstanceGraph from "@components/extension/workflows/WorkflowInstanceGraph";
import PageSection from "@components/common/PageSection";
import WorkflowNodeExecutorContextProvider from "@components/extension/workflows/WorkflowNodeExecutorContext";
import WorkflowInstanceContext from "@components/extension/workflows/WorkflowInstanceContext";

export default function WorkflowInstanceView({id}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [instance, setInstance] = useState({
        id: '',
        workflow: {
            name: ''
        }
    })

    const [items, setItems] = useState([])

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query WorkflowInstance($id: String!) {
                        workflowInstance(id: $id) {
                            id
                            status
                            finished
                            startTime
                            endTime
                            durationMs
                            workflow {
                                name
                                nodes {
                                    id
                                    executorId
                                    data
                                    parents {
                                        id
                                    }
                                }
                            }
                            context {
                                data {
                                    key
                                    value
                                }
                            }
                            nodesExecutions {
                                id
                                status
                                output
                                error
                                startTime
                                endTime
                                durationMs
                            }
                        }
                    }
                `,
                {id}
            ).then(data => {
                const instance = data.workflowInstance
                setInstance(instance)

                setItems([
                    {
                        key: 'workflow',
                        label: 'Workflow',
                        children: instance.workflow.name,
                        span: 4,
                    },
                    {
                        key: 'id',
                        label: 'ID',
                        children: instance.id,
                        span: 4,
                    },
                    {
                        key: 'status',
                        label: 'Status',
                        children: <WorkflowInstanceStatus status={instance.status}/>,
                        span: 4,
                    },
                    {
                        key: 'startTime',
                        label: 'Start time',
                        children: <TimestampText value={instance.startTime}/>,
                        span: 4,
                    },
                    {
                        key: 'endTime',
                        label: 'End time',
                        children: <TimestampText value={instance.endTime}/>,
                        span: 4,
                    },
                    {
                        key: 'duration',
                        label: 'Duration',
                        children: <DurationMs ms={instance.durationMs}/>,
                        span: 4,
                    },
                    {
                        key: 'context',
                        label: 'Context',
                        children: <WorkflowInstanceContext instance={instance}/>,
                        span: 12,
                    }
                ])
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id]);

    return (
        <>
            <Head>
                {pageTitle(`Workflow | ${instance.workflow.name} [${instance.id}]`)}
            </Head>
            <MainPage
                title={`${instance.workflow.name} [${instance.id}]`}
                breadcrumbs={
                    homeBreadcrumbs().concat([
                        <Link key="audit" href="/extension/workflows/audit">Workflows audit</Link>,
                    ])
                }
                commands={[
                    <CloseCommand key="home" href="/extension/workflows/audit"/>
                ]}
            >
                <WorkflowNodeExecutorContextProvider>
                    <Skeleton loading={loading} active>
                        <Space direction="vertical">
                            <Descriptions
                                items={items}
                                column={12}
                            />
                            <PageSection
                                title={undefined}
                                padding={false}>
                                <WorkflowInstanceGraph instance={instance}/>
                            </PageSection>
                        </Space>
                    </Skeleton>
                </WorkflowNodeExecutorContextProvider>
            </MainPage>
        </>
    )
}