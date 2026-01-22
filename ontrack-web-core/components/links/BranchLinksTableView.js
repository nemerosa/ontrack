import Head from "next/head";
import {subBranchTitle} from "@components/common/Titles";
import {downToBranchBreadcrumbs} from "@components/common/Breadcrumbs";
import BranchLinksModeButton from "@components/links/BranchLinksModeButton";
import {FaArrowRight, FaCaretRight, FaProjectDiagram} from "react-icons/fa";
import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import {branchQuery} from "@components/links/BranchDependenciesFragments";
import {useBranch} from "@components/services/fragments";
import StandardTable from "@components/common/table/StandardTable";
import BuildLink from "@components/builds/BuildLink";
import {Divider, Space, Typography} from "antd";
import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";
import BuildPromotions from "@components/links/BuildPromotions";

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
        const {downstreamLinks, ...strippedBranch} = branch
        branch.downstreamLinks.forEach(downstreamLink => {
            const latestBuilds = strippedBranch.latestBuilds
            let latestBuild = undefined
            if (latestBuilds && latestBuilds.length > 0) {
                latestBuild = latestBuilds[0]
            }
            const link = {
                branch: strippedBranch,
                latestBuild,
                qualifier: downstreamLink.qualifier,
                sourceBuild: downstreamLink.sourceBuild,
                targetBuild: downstreamLink.targetBuild,
                autoVersioning: downstreamLink.autoVersioning,
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
                                key: 'data',
                                title: 'Data',
                                render: (_, link) => JSON.stringify(link)
                            },
                            {
                                key: 'consumer',
                                title: 'Consumer',
                                render: (_, link) => <Space direction="vertical">
                                    <Space size="small">
                                        <ProjectLink project={link.sourceBuild.branch.project}/>
                                        <Divider type="vertical"/>
                                        <BranchLink branch={link.sourceBuild.branch}/>
                                    </Space>
                                    <BuildLink build={link.sourceBuild}/>
                                    <BuildPromotions build={link.sourceBuild}/>
                                </Space>,
                            },
                            {
                                key: 'dependency',
                                title: 'Dependency',
                                render: (_, link) => <Space direction="vertical">
                                    <Space size="small">
                                        <ProjectLink project={link.targetBuild.branch.project}/>
                                        {
                                            link.qualifier &&
                                            <Typography.Text>[{link.qualifier}]</Typography.Text>
                                        }
                                        <Divider type="vertical"/>
                                        <BranchLink branch={link.targetBuild.branch}/>
                                    </Space>
                                    <Space size="small">
                                        <FaArrowRight/>
                                        <BuildLink build={link.targetBuild}/>
                                    </Space>
                                    <BuildPromotions build={link.targetBuild}/>
                                </Space>,
                            },
                            {
                                key: 'latestBuild',
                                title: 'Latest build',
                                render: (_, link) => <>
                                    {
                                        link.latestBuild &&
                                        <Space direction="vertical">
                                            <Space size="small">
                                                <FaCaretRight/>
                                                <BuildLink build={link.latestBuild}/>
                                            </Space>
                                            <BuildPromotions build={link.latestBuild}/>
                                        </Space>
                                    }
                                </>,
                            },
                        ]}
                    />
                </LoadingContainer>
            </MainPage>
        </>
    )
}