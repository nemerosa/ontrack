import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import BranchLinksModeButton from "@components/links/BranchLinksModeButton";
import {FaArrowRight, FaProjectDiagram} from "react-icons/fa";
import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import {branchQuery} from "@components/links/BranchDependenciesFragments";
import {useBranch} from "@components/services/fragments";
import StandardTable from "@components/common/table/StandardTable";
import {Space, Typography} from "antd";
import BranchNodeComponent from "@components/links/BranchNodeComponent";
import BranchLinkNodeComponent from "@components/links/BranchLinkNodeComponent";

export default function BranchLinksTableView({id}) {

    const {loading, branch} = useBranch(id)
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (branch.id) {
            setCommands([
                <CloseCommand key="close" href={branchUri(branch)}/>,
            ])
        }
    }, [branch])

    const flattenDeepDependencies = (data, links) => {
        const branch = data.branch
        branch.downstreamLinks.forEach(downstreamLink => {
            const link = {
                branch,
                qualifier: downstreamLink.qualifier,
                sourceBuild: downstreamLink.sourceBuild,
                targetBuild: downstreamLink.targetBuild,
                autoVersioning: downstreamLink.autoVersioning,
                targetBranch: downstreamLink.branch,
            }
            links.push(link)
            flattenDeepDependencies(downstreamLink, links)
        })
    }

    const flattenDependencies = (data) => {
        const links = []
        flattenDeepDependencies(data, links)
        return {
            pageInfo: {},
            pageItems: links,
        }
    }

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
                    <StandardTable
                        id="branch-dependencies"
                        filter={{}}
                        variables={{branchId: Number(id)}}
                        query={branchQuery({downstream: true})}
                        queryNode={data => flattenDependencies(data)}
                        columns={[
                            {
                                key: 'consumer',
                                title: 'Consumer',
                                render: (_, link) => <BranchNodeComponent
                                    branch={link.branch}
                                />
                            },
                            {
                                key: 'link',
                                title: <Space size="small">
                                    <FaArrowRight/>
                                    <Typography.Text>depends on</Typography.Text>
                                    <FaArrowRight/>
                                </Space>,
                                render: (_, link) => <BranchLinkNodeComponent
                                    link={link}
                                    sourceBranch={link.branch}
                                    targetBranch={link.targetBranch}
                                />
                            },
                            {
                                key: 'dependency',
                                title: 'Dependency',
                                render: (_, link) => <BranchNodeComponent
                                    branch={link.targetBranch}
                                />
                            },
                        ]}
                    />
                </LoadingContainer>
            </MainPage>
        </>
    )
}