import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {
    gqlEnvironmentData,
    gqlSlotData,
    gqlSlotPipelineData
} from "@components/extension/environments/EnvironmentGraphQL";
import {slotBreadcrumbs, slotTitle, slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {CloseCommand} from "@components/common/Commands";
import {useReloadState} from "@components/common/StateUtils";
import EnvironmentsWarning from "@components/extension/environments/EnvironmentsWarning";
import {Space} from "antd";
import SlotPipelineSummary from "@components/extension/environments/SlotPipelineSummary";
import PageSection from "@components/common/PageSection";
import SlotPipelineSteps from "@components/extension/environments/SlotPipelineSteps";
import DeleteDeploymentCommand from "@components/extension/environments/DeleteDeploymentCommand";

export default function SlotPipelineView({id}) {

    const client = useGraphQLClient()

    const [reloadState, reload] = useReloadState()
    const [loading, setLoading] = useState(true)
    const [title, setTitle] = useState('')
    const [pipelinePageTitle, setPipelinePageTitle] = useState('')
    const [breadcrumbs, setBreadcrumbs] = useState([])
    const [commands, setCommands] = useState([])
    const [pipeline, setPipeline] = useState()

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query PipelineDetails($id: String!) {
                        slotPipelineById(id: $id) {
                            ...SlotPipelineData
                            slot {
                                ...SlotData
                                authorizations {
                                    name
                                    action
                                    authorized
                                }
                                environment {
                                    ...EnvironmentData
                                }
                            }
                        }
                    }
                    ${gqlSlotPipelineData}
                    ${gqlSlotData}
                    ${gqlEnvironmentData}
                `,
                {id}
            ).then(data => {
                const pipeline = data.slotPipelineById
                setPipeline(pipeline)
                // Title
                setTitle(`Deployment #${pipeline.number}`)
                setPipelinePageTitle(`${slotTitle(pipeline.slot)} | #${pipeline.number}`)
                // Breadcrumbs
                setBreadcrumbs(slotBreadcrumbs(pipeline.slot))
                // Commands
                const commands = []
                commands.push(
                    <DeleteDeploymentCommand key="delete" deployment={pipeline}/>,
                    <CloseCommand key="close" href={slotUri(pipeline.slot)}/>
                )
                setCommands(commands)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id, reloadState])

    return (
        <>
            <Head>
                {pageTitle(pipelinePageTitle)}
            </Head>
            <MainPage
                title={title}
                warning={<EnvironmentsWarning/>}
                breadcrumbs={breadcrumbs}
                commands={commands}
            >
                <Space direction="vertical">
                    <SlotPipelineSummary pipelineId={id} reloadState={reloadState}/>
                    <PageSection
                        title="Deployment status"
                        padding={true}
                    >
                        <SlotPipelineSteps pipelineId={id} reloadState={reloadState} onChange={reload}/>
                    </PageSection>
                </Space>
            </MainPage>
        </>
    )
}