import Head from "next/head";
import {buildTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {buildBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {CloseCommand, Command} from "@components/common/Commands";
import {branchUri, buildLinksUri} from "@components/common/Links";
import {
    gqlDecorationFragment,
    gqlInformationFragment,
    gqlPropertiesFragment,
    gqlUserMenuActionFragment
} from "@components/services/fragments";
import BuildContent from "@components/builds/BuildContent";
import {Space} from "antd";
import Decorations from "@components/framework/decorations/Decorations";
import BuildInfoViewDrawer from "@components/builds/BuildInfoViewDrawer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import StoredGridLayoutResetCommand from "@components/grid/StoredGridLayoutResetCommand";
import StoredGridLayoutContextProvider from "@components/grid/StoredGridLayoutContext";
import {FaProjectDiagram} from "react-icons/fa";
import UserMenuActions from "@components/entities/UserMenuActions";
import EditBuildCommand from "@components/builds/EditBuildCommand";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {useRefresh} from "@components/common/RefreshUtils";

export default function BuildView({id}) {

    const client = useGraphQLClient()

    const [loadingBuild, setLoadingBuild] = useState(true)
    const [build, setBuild] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    const [refreshState, refresh] = useRefresh()

    useEffect(() => {
        if (client && id) {
            setLoadingBuild(true)
            client.request(
                gql`
                    query GetBuild($id: Int!) {
                        build(id: $id) {
                            id
                            name
                            description
                            annotatedDescription
                            creation {
                                user
                                time
                            }
                            userMenuActions {
                                ...userMenuActionFragment
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
                            authorizations {
                                name
                                action
                                authorized
                            }
                        }
                    }

                    ${gqlDecorationFragment}
                    ${gqlPropertiesFragment}
                    ${gqlInformationFragment}
                    ${gqlUserMenuActionFragment}
                `,
                {id: Number(id)}
            ).then(data => {
                setBuild(data.build)
                setLoadingBuild(false)
                setCommands([
                    <UserMenuActions
                        key="tools"
                        actions={data.build.userMenuActions}
                    />,
                    <Command
                        key="links"
                        icon={<FaProjectDiagram/>}
                        href={buildLinksUri(data.build)}
                        text="Links"
                        title="Displays downstream and upstream dependencies"
                    />,
                    <EditBuildCommand
                        build={data.build}
                        onSuccess={refresh}
                        key="edit"
                    />,
                    <StoredGridLayoutResetCommand key="reset"/>,
                    <CloseCommand key="close" href={branchUri(data.build.branch)}/>,
                ])
            })
        }
    }, [client, id, refreshState])

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
                            <AnnotatedDescription entity={build} type="secondary" disabled={false}/>
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