import Head from "next/head";
import {buildTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {buildBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {CloseCommand, Command, LegacyLinkCommand} from "@components/common/Commands";
import {branchUri, buildLegacyUri, buildLinksUri} from "@components/common/Links";
import {gqlDecorationFragment, gqlInformationFragment, gqlPropertiesFragment} from "@components/services/fragments";
import BuildContent from "@components/builds/BuildContent";
import {Space} from "antd";
import Decorations from "@components/framework/decorations/Decorations";
import BuildInfoViewDrawer from "@components/builds/BuildInfoViewDrawer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import StoredGridLayoutResetCommand from "@components/grid/StoredGridLayoutResetCommand";
import StoredGridLayoutContextProvider from "@components/grid/StoredGridLayoutContext";
import {FaProjectDiagram} from "react-icons/fa";

export default function BuildView({id}) {

    const client = useGraphQLClient()

    const [loadingBuild, setLoadingBuild] = useState(true)
    const [build, setBuild] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (client && id) {
            setLoadingBuild(true)
            client.request(
                gql`
                    query GetBuild($id: Int!) {
                        build(id: $id) {
                            id
                            name
                            creation {
                                user
                                time
                            }
                            releaseProperty {
                                value
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
                    <StoredGridLayoutResetCommand key="reset"/>,
                    <Command
                        key="links"
                        icon={<FaProjectDiagram/>}
                        href={buildLinksUri(data.build)}
                        text="Links"
                        title="Displays downstream and upstream dependencies"
                    />,
                    <LegacyLinkCommand
                        key="legacy"
                        href={buildLegacyUri(data.build)}
                        text="Legacy build"
                        title="Goes to the legacy build page"
                    />,
                    <CloseCommand key="close" href={branchUri(data.build.branch)}/>,
                ])
            })
        }
    }, [client, id])

    return (
        <>
            <Head>
                {buildTitle(build)}
            </Head>
            <StoredGridLayoutContextProvider>
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
            </StoredGridLayoutContextProvider>
        </>
    )
}