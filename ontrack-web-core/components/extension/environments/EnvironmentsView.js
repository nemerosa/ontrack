import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import MainPage from "@components/layouts/MainPage";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import EnvironmentCreateCommand from "@components/extension/environments/EnvironmentCreateCommand";
import LoadingContainer from "@components/common/LoadingContainer";
import {useEventForRefresh} from "@components/common/EventsContext";
import EnvironmentList from "@components/extension/environments/EnvironmentList";
import SlotCreateCommand from "@components/extension/environments/SlotCreateCommand";
import {gqlSlotData} from "@components/extension/environments/EnvironmentGraphQL";

export default function EnvironmentsView() {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [environments, setEnvironments] = useState([])
    const [commands, setCommands] = useState([])

    const environmentCreated = useEventForRefresh("environment.created")
    const slotCreated = useEventForRefresh("slot.created")

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query EnvironmentList {
                        environments {
                            id
                            name
                            description
                            order
                            tags
                            image
                            slots {
                                ...SlotData
                            }
                        }
                    }

                    ${gqlSlotData}
                `
            ).then(data => {
                setEnvironments(data.environments)
                setCommands([
                    <EnvironmentCreateCommand key="create-environment"/>,
                    <SlotCreateCommand key="create-slot"/>,
                    <CloseCommand key="close" href={homeUri()}/>,
                ])
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, environmentCreated, slotCreated])

    return (
        <>
            <Head>
                {pageTitle("Environments")}
            </Head>
            <MainPage
                title="Environments"
                breadcrumbs={homeBreadcrumbs()}
                commands={commands}
            >
                <LoadingContainer loading={loading}>
                    <EnvironmentList environments={environments}/>
                </LoadingContainer>
            </MainPage>
        </>
    )
}