import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import BranchLinksModeButton from "@components/links/BranchLinksModeButton";
import {FaProjectDiagram} from "react-icons/fa";
import MainPage from "@components/layouts/MainPage";
import {useBranch} from "@components/services/fragments";
import {useState} from "react";

export default function BranchLinksTableView({id}) {

    const {loading, branch} = useBranch(id)
    const [commands, setCommands] = useState([])

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
            </MainPage>
        </>
    )
}