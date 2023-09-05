import Head from "next/head";
import {buildTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {buildBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import {gqlDecorationFragment, gqlInformationFragment, gqlPropertiesFragment} from "@components/services/fragments";
import BuildContent from "@components/builds/BuildContent";
import {Space} from "antd";
import Decorations from "@components/framework/decorations/Decorations";
import BuildInfoViewDrawer from "@components/builds/BuildInfoViewDrawer";

export default function BuildView({id}) {

    const [loadingBuild, setLoadingBuild] = useState(true)
    const [build, setBuild] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (id) {
            setLoadingBuild(true)
            graphQLCall(
                gql`
                    query GetBuild($id: Int!) {
                        build(id: $id) {
                            id
                            name
                            creation {
                                user
                                time
                            }
                            properties {
                                ...propertiesFragment
                            }
                            information {
                                ...informationFragment
                            }
                            decorations {
                                ...decorationContent
                            }
                            branch {
                                id
                                name
                                project {
                                    id
                                    name
                                }
                            }
                        }
                    }

                    ${gqlDecorationFragment}
                    ${gqlPropertiesFragment}
                    ${gqlInformationFragment}
                `,
                {id}
            ).then(data => {
                setBuild(data.build)
                setLoadingBuild(false)
                setCommands([
                    <CloseCommand key="close" href={branchUri(data.build.branch)}/>,
                ])
            })
        }
    }, [id])

    return (
        <>
            <Head>
                {buildTitle(build)}
            </Head>
            <MainPage
                title={
                    <Space>
                        {build.name}
                        <Decorations entity={build}/>
                    </Space>
                }
                breadcrumbs={buildBreadcrumbs(build)}
                commands={commands}
            >
                <LoadingContainer loading={loadingBuild} tip="Loading build">
                    <BuildContent build={build}/>
                    <BuildInfoViewDrawer build={build} loading={loadingBuild}/>
                </LoadingContainer>
            </MainPage>
        </>
    )
}