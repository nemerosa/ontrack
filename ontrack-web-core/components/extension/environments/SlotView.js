import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import MainPage from "@components/layouts/MainPage";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import {gqlEnvironmentData, gqlSlotData} from "@components/extension/environments/EnvironmentGraphQL";
import {environmentsBreadcrumbs, environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {CloseCommand} from "@components/common/Commands";
import {Col, Row} from "antd";
import SlotDetails from "@components/extension/environments/SlotDetails";
import PageSection from "@components/common/PageSection";
import SlotPipelinesTable from "@components/extension/environments/SlotPipelinesTable";
import SlotEligibleBuildsTable from "@components/extension/environments/SlotEligibleBuildsTable";
import {useReloadState} from "@components/common/StateUtils";

export default function SlotView({id}) {

    const client = useGraphQLClient()

    const [reloadState, reload] = useReloadState()

    const [loading, setLoading] = useState(true)
    const [slot, setSlot] = useState()
    const [title, setTitle] = useState(`Slot ${id}`)

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    query Slot($id: String!) {
                        slotById(id: $id) {
                            ...SlotData
                            environment {
                                ...EnvironmentData
                            }
                        }
                    }

                    ${gqlEnvironmentData}
                    ${gqlSlotData}
                `,
                {id}
            ).then(data => {
                const slot = data.slotById;
                setSlot(slot)
                let title = `Slot ${slot.environment.name} - ${slot.project.name}`
                if (slot.qualifier) {
                    title += ` [${slot.qualifier}]`
                }
                setTitle(title)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id, reloadState])

    return (
        <>
            <Head>
                {pageTitle(title)}
            </Head>
            <MainPage
                title={title}
                breadcrumbs={environmentsBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={environmentsUri}/>
                ]}
            >
                <LoadingContainer loading={loading}>
                    <Row gutter={[16, 16]} wrap>
                        <Col span={12}>
                            <PageSection
                                title="Slot details"
                                padding={true}
                            >
                                <SlotDetails slot={slot}/>
                            </PageSection>
                        </Col>
                        <Col span={12}>
                            <PageSection
                                title="Eligible builds"
                                padding={false}
                            >
                                <SlotEligibleBuildsTable slot={slot} onChange={reload}/>
                            </PageSection>
                        </Col>
                        <Col span={24}>
                            <PageSection
                                title="Pipelines"
                                padding={false}
                            >
                                <SlotPipelinesTable slot={slot}/>
                            </PageSection>
                        </Col>
                    </Row>
                </LoadingContainer>
            </MainPage>
        </>
    )
}