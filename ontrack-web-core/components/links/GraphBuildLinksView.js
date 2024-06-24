import {useEffect, useState} from "react";
import {CloseCommand} from "@components/common/Commands";
import {buildUri} from "@components/common/Links";
import Head from "next/head";
import {subBuildTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {downToBuildBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import PageSection from "@components/common/PageSection";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import BuildLinksGraph from "@components/links/BuildLinksGraph";

export default function GraphBuildLinksView({id}) {

    const client = useGraphQLClient()

    const [loadingBuild, setLoadingBuild] = useState(true)
    const [build, setBuild] = useState({branch: {project: ''}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (id && client) {
            setLoadingBuild(true)
            client.request(
                gql`
                    query GetBuild($id: Int!) {
                        build(id: $id) {
                            id
                            name
                            branch {
                                id
                                name
                                project {
                                    id
                                    name
                                }
                            }
                            releaseProperty {
                                value
                            }
                        }
                    }
                `,
                {id}
            ).then(data => {
                const build = data.build
                setBuild(build)
                setCommands([
                    <CloseCommand key="close" href={buildUri(build)}/>,
                ])
            }).finally(() => {
                setLoadingBuild(false)
            })
        }
    }, [id, client])

    return (
        <>
            <Head>
                {subBuildTitle(build, "Links")}
            </Head>
            <MainPage
                title="Links"
                breadcrumbs={downToBuildBreadcrumbs({build: build})}
                commands={commands}
            >
                <LoadingContainer loading={loadingBuild} tip="Loading build">
                    <PageSection title={undefined}
                                 padding={false}
                    >
                        <BuildLinksGraph build={build}/>
                    </PageSection>
                </LoadingContainer>
            </MainPage>
        </>
    )
}