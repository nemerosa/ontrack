import {useEffect, useState} from "react";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import PageSection from "@components/common/PageSection";
import BranchLinksGraph from "@components/links/BranchLinksGraph";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function BranchLinksView({id}) {

    const client = useGraphQLClient()

    const [loadingBranch, setLoadingBranch] = useState(true)
    const [branch, setBranch] = useState({project: {}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (id && client) {
            setLoadingBranch(true)
            client.request(
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
                const branch = data.branches[0]
                setBranch(branch)
                setCommands([
                    <CloseCommand key="close" href={branchUri(branch)}/>,
                ])
            }).finally(() => {
                setLoadingBranch(false)
            })
        }
    }, [id, client])

    return (
        <>
            <Head>
                {subBranchTitle(branch, "Links")}
            </Head>
            <MainPage
                title="Links"
                breadcrumbs={downToBranchBreadcrumbs({branch})}
                commands={commands}
            >
                <LoadingContainer loading={loadingBranch} tip="Loading branch">
                    <PageSection title={undefined}
                                 padding={false}
                                 fullHeight={true}>
                        <BranchLinksGraph branch={branch}/>
                    </PageSection>
                </LoadingContainer>
            </MainPage>
        </>
    )
}