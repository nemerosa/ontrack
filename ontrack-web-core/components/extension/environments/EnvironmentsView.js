import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import MainPage from "@components/layouts/MainPage";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";

export default function EnvironmentsView() {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [environments, setEnvironments] = useState([])

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
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>
                ]}
            >
                {JSON.stringify(environments)}
            </MainPage>
        </>
    )
}