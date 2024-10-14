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

export default function EnvironmentsView() {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [environments, setEnvironments] = useState([])
    const [commands, setCommands] = useState([])

    const environmentCreated = useEventForRefresh("environment.created")

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
                        }
                    }
                `
            ).then(data => {
                setEnvironments(data.environments)
                setCommands([
                    <EnvironmentCreateCommand key="create"/>,
                    <CloseCommand key="close" href={homeUri()}/>,
                ])
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, environmentCreated])

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