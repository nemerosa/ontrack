import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import MainPage from "@components/layouts/MainPage";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {UserContext} from "@components/providers/UserProvider";
import EnvironmentCreateCommand from "@components/extension/environments/EnvironmentCreateCommand";
import LoadingContainer from "@components/common/LoadingContainer";

export default function EnvironmentsView() {

    const client = useGraphQLClient()
    const user = useContext(UserContext)

    const [loading, setLoading] = useState(false)
    const [environments, setEnvironments] = useState([])
    const [commands, setCommands] = useState([])

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
                const commands = []
                if (user.authorizations.environment?.create) {
                    commands.push(
                        <EnvironmentCreateCommand key="create"/>
                    )
                }
                commands.push(
                    <CloseCommand key="close" href={homeUri()}/>
                )
                setCommands(commands)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client])

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
                    {JSON.stringify(environments)}
                </LoadingContainer>
            </MainPage>
        </>
    )
}