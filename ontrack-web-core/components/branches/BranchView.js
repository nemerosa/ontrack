import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {CloseCommand} from "@components/common/Commands";
import {projectUri} from "@components/common/Links";
import Head from "next/head";
import {branchTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {branchBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import {BranchViewContextProvider} from "@components/branches/BranchViewContext";
import BranchInfoViewExpandButton from "@components/branches/BranchInfoViewExpandButton";
import BranchInfoViewDrawer from "@components/branches/BranchInfoViewDrawer";
import BranchContent from "@components/branches/BranchContent";

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
                        }
                    }
                `,
                {id}
            ).then(data => {
                let branch = data.branches[0];
                setBranch(branch)
                setLoadingBranch(false)
                setCommands([
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
                title={branch.name}
                breadcrumbs={branchBreadcrumbs(branch)}
                commands={commands}
            >
                <LoadingContainer loading={loadingBranch} tip="Loading branch">
                    <BranchViewContextProvider>
                        <BranchContent branch={branch}/>
                        <BranchInfoViewExpandButton/>
                        <BranchInfoViewDrawer/>
                    </BranchViewContextProvider>
                </LoadingContainer>
            </MainPage>
        </>
    )
}