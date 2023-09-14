import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {CloseCommand, LegacyLinkCommand} from "@components/common/Commands";
import {branchLegacyUri, projectLegacyUri, projectUri} from "@components/common/Links";
import Head from "next/head";
import {branchTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {branchBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import {BranchViewContextProvider} from "@components/branches/BranchViewContext";
import BranchInfoViewDrawer from "@components/branches/BranchInfoViewDrawer";
import BranchContent from "@components/branches/BranchContent";
import {Space} from "antd";
import BranchFavourite from "@components/branches/BranchFavourite";
import {gqlInformationFragment, gqlPropertiesFragment} from "@components/services/fragments";

export default function BranchView({id}) {
    const [loadingBranch, setLoadingBranch] = useState(true)
    const [branch, setBranch] = useState({project: {}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (id) {
            setLoadingBranch(true)
            graphQLCall(
                gql`
                    query GetBranch($id: Int!) {
                        branches(id: $id) {
                            id
                            name
                            project {
                                id
                                name
                            }
                            properties {
                                ...propertiesFragment
                            }
                            information {
                                ...informationFragment
                            }
                        }
                    }
                    ${gqlPropertiesFragment}
                    ${gqlInformationFragment}
                `,
                {id}
            ).then(data => {
                let branch = data.branches[0];
                setBranch(branch)
                setLoadingBranch(false)
                setCommands([
                    <LegacyLinkCommand
                        key="legacy"
                        href={branchLegacyUri(branch)}
                        text="Legacy branch"
                        title="Goes to the legacy branch page"
                    />,
                    <CloseCommand key="close" href={projectUri(branch.project)}/>,
                ])
            })
        }
    }, [id])

    return (
        <>
            <Head>
                {branchTitle(branch)}
            </Head>
            <MainPage
                title={
                    <Space>
                        {branch.name}
                        <BranchFavourite branch={branch}/>
                    </Space>
                }
                breadcrumbs={branchBreadcrumbs(branch)}
                commands={commands}
            >
                <LoadingContainer loading={loadingBranch} tip="Loading branch">
                    <BranchViewContextProvider>
                        <BranchContent branch={branch}/>
                        <BranchInfoViewDrawer branch={branch} loadingBranch={loadingBranch}/>
                    </BranchViewContextProvider>
                </LoadingContainer>
            </MainPage>
        </>
    )
}