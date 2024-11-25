import {useContext, useEffect, useState} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Table} from "antd";
import EnvironmentName from "@components/extension/environments/EnvironmentName";
import {gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import SlotTitle from "@components/extension/environments/SlotTitle";
import EnvironmentQualifiedProject from "@components/widgets/extension/environments/EnvironmentQualifiedProject";

export default function EnvironmentListWidget({title = '', tags = [], projects = []}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [environments, setEnvironments] = useState([])
    const [qualifiedProjects, setQualifiedProjects] = useState([])
    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query EnvironmentList(
                        $tags: [String!]!,
                        $projects: [String!],
                    ) {
                        environments(filter: {tags: $tags, projects: $projects}) {
                            id
                            name
                            order
                            tags
                            slots(projects: $projects) {
                                id
                                project {
                                    id
                                    name
                                }
                                qualifier
                                lastDeployedPipeline {
                                    ...SlotPipelineData
                                }
                            }
                        }
                    }
                    ${gqlSlotPipelineData}
                `,
                {
                    tags,
                    projects,
                }
            ).then(data => {
                setEnvironments(data.environments)
                // Grouping slots per qualified projects
                const qualifiedProjects = []
                data.environments.forEach(environment => {
                    environment.slots.forEach(slot => {
                        const qualifiedProject = {
                            key: `${slot.project.name}-${slot.qualifier}`,
                            project: slot.project,
                            qualifier: slot.qualifier,
                        }
                        const existingQualifiedProject = qualifiedProjects.find(it => it.key === qualifiedProject.key)
                        if (!existingQualifiedProject) {
                            qualifiedProjects.push(qualifiedProject)
                        }
                    })
                })
                setQualifiedProjects(qualifiedProjects)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, tags, projects])

    const {setTitle} = useContext(DashboardWidgetCellContext)
    useEffect(() => {
        setTitle(title ? title : "Environments")
    }, [title])

    return (
        <>
            <Table
                loading={loading}
                dataSource={environments}
                pagination={false}
            >

                <Table.Column
                    key="name"
                    title="Environment"
                    render={(_, environment) => <EnvironmentName environment={environment}/>}
                />

                {
                    qualifiedProjects.map(qualifiedProject => <Table.Column
                        key={qualifiedProject.key}
                        title={<SlotTitle slot={qualifiedProject}/>}
                        render={(_, environment) => <EnvironmentQualifiedProject
                            environment={environment}
                            qualifiedProject={qualifiedProject}
                        />}
                    />)
                }

            </Table>
        </>
    )

}