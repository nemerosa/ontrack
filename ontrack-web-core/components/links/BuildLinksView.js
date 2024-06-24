import {useEffect, useState} from "react";
import {CloseCommand, Command} from "@components/common/Commands";
import {buildUri} from "@components/common/Links";
import Head from "next/head";
import {subBuildTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {downToBuildBreadcrumbs} from "@components/common/Breadcrumbs";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {Skeleton} from "antd";
import {getLocallySelectedDependencyLinksMode, setLocallySelectedDependencyLinksMode} from "@components/storage/local";
import {FaProjectDiagram, FaStream} from "react-icons/fa";
import DependencyLinksModeButton from "@components/links/DependencyLinksModeButton";
import BuildLinksGraph from "@components/links/BuildLinksGraph";

export default function BuildLinksView({id}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [build, setBuild] = useState({branch: {project: ''}})
    const [commands, setCommands] = useState([])

    const [dependencyLinksMode, setDependencyLinksMode] = useState('')

    const changeDependencyLinksMode = (mode) => {
        setDependencyLinksMode(mode)
        setLocallySelectedDependencyLinksMode(mode)
    }

    useEffect(() => {
        if (id && client) {
            setLoading(true)
            client.request(
                gql`
                    query GetBuild($id: Int!) {
                        build(id: $id) {
                            id
                            name
                            branch {
                                id
                                name
                                project {
                                    id
                                    name
                                }
                            }
                            releaseProperty {
                                value
                            }
                        }
                    }
                `,
                {id}
            ).then(data => {
                const build = data.build
                setBuild(build)

                setCommands([
                    <CloseCommand key="close" href={buildUri(build)}/>,
                ])

                setDependencyLinksMode(getLocallySelectedDependencyLinksMode() ?? 'graph')
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [id, client])

    return (
        <>
            <Head>
                {subBuildTitle(build, "Links")}
            </Head>
            <MainPage
                title="Links"
                breadcrumbs={downToBuildBreadcrumbs({build: build})}
                commands={commands}
            >
                <DependencyLinksModeButton
                    key="graph"
                    icon={<FaProjectDiagram/>}
                    selectedMode={dependencyLinksMode}
                    mode="graph"
                    action={changeDependencyLinksMode}
                    title="Displays the dependencies as a graph"
                />
                <DependencyLinksModeButton
                    key="tree"
                    icon={<FaStream/>}
                    selectedMode={dependencyLinksMode}
                    mode="tree"
                    action={changeDependencyLinksMode}
                    title="Displays the dependencies as a tree"
                />
                <Skeleton active loading={loading}>
                    {
                        dependencyLinksMode === 'graph' && <BuildLinksGraph build={build}/>
                    }
                </Skeleton>
            </MainPage>
        </>
    )
}