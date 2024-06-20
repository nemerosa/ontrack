import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import MainPage from "@components/layouts/MainPage";
import Link from "next/link";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useContext, useEffect, useState} from "react";
import {Descriptions, Skeleton, Space} from "antd";
import {gql} from "graphql-request";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import WorkflowInstanceGraph from "@components/extension/workflows/WorkflowInstanceGraph";
import PageSection from "@components/common/PageSection";
import WorkflowNodeExecutorContextProvider from "@components/extension/workflows/WorkflowNodeExecutorContext";
import WorkflowInstanceContext from "@components/extension/workflows/WorkflowInstanceContext";
import {UserContext} from "@components/providers/UserProvider";
import WorkflowInstanceStopButton from "@components/extension/workflows/WorkflowInstanceStopButton";

export default function WorkflowInstanceView({id}) {

    const client = useGraphQLClient()
    const user = useContext(UserContext)

    const [loading, setLoading] = useState(true)
    const [loadingCount, setLoadingCount] = useState(0)
    const [instance, setInstance] = useState({
        id: '',
        workflow: {
            name: ''
        }
    })

    const reload = () => {
        setLoadingCount(count => count + 1)
    }

    const [items, setItems] = useState([])

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query WorkflowInstance($id: String!) {
                        workflowInstance(id: $id) {
                            id
                            timestamp
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
                                    timeout
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
                        children: <Space>
                            <WorkflowInstanceStatus status={instance.status}/>
                            {
                                user.authorizations.workflow?.stop &&
                                (instance.status === 'STARTED' || instance.status === 'RUNNING') &&
                                <WorkflowInstanceStopButton id={instance.id} onStopped={reload}/>
                            }
                        </Space>,
                        span: 4,
                    },
                    {
                        key: 'startTime',
                        label: 'Start time',
                        children: <TimestampText value={instance.startTime} format="YYYY MMM DD, HH:mm:ss"/>,
                        span: 3,
                    },
                    {
                        key: 'endTime',
                        label: 'End time',
                        children: <TimestampText value={instance.endTime} format="YYYY MMM DD, HH:mm:ss"/>,
                        span: 3,
                    },
                    {
                        key: 'duration',
                        label: 'Duration',
                        children: <DurationMs ms={instance.durationMs}/>,
                        span: 3,
                    },
                    {
                        key: 'timestamp',
                        label: 'Last update',
                        children: <TimestampText value={instance.timestamp} format="YYYY MMM DD, HH:mm:ss"/>,
                        span: 3,
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
    }, [client, id, loadingCount]);

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