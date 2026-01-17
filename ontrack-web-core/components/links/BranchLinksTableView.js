import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import BranchLinksModeButton from "@components/links/BranchLinksModeButton";
import {FaProjectDiagram} from "react-icons/fa";
import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import {useQuery} from "@components/services/GraphQL";
import {branchQuery} from "@components/links/BranchDependenciesFragments";
import JsonDisplay from "@components/common/JsonDisplay";

export default function BranchLinksTableView({id}) {

    const [commands, setCommands] = useState([])

    const {loading, data: branch} = useQuery(
        branchQuery({downstream: true}),
        {
            variables: {
                branchId: Number(id),
            },
            dataFn: data => data.branch,
            initialData: {project: {}},
        }
    )

    useEffect(() => {
        if (branch.id) {
            setCommands([
                <CloseCommand key="close" href={branchUri(branch)}/>,
            ])
        }
    }, [branch])

    return (
        <>
            <Head>
                {subBranchTitle(branch, "Links")}
            </Head>
            <MainPage
                title="Links table"
                breadcrumbs={downToBranchBreadcrumbs({branch})}
                commands={commands}
            >
                <BranchLinksModeButton
                    icon={<FaProjectDiagram/>}
                    mode="graph"
                    title="Displays the dependencies as a graph"
                    href={`/branch/${id}/links`}
                />
                <LoadingContainer loading={loading}>
                    <JsonDisplay value={JSON.stringify(branch, null, 2)}/>
                </LoadingContainer>
            </MainPage>
        </>
    )
}