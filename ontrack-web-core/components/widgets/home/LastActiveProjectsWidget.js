import {useContext, useEffect, useState} from "react";
import RowTag from "@components/common/RowTag";
import ProjectBox from "@components/projects/ProjectBox";
import {Empty, Space, Typography} from "antd";
import {useEventForRefresh} from "@components/common/EventsContext";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import PaddedContent from "@components/common/PaddedContent";

export default function LastActiveProjectsWidget({count}) {

    const client = useGraphQLClient()
    const [projects, setProjects] = useState([])
    const projectsRefreshCount = useEventForRefresh("project.created")
    const favouritesRefresh = useEventForRefresh("project.favourite")

    const {setTitle} = useContext(DashboardWidgetCellContext)

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query LastActiveProjects($count: Int! = 10) {
                        lastActiveProjects(count: $count) {
                            id
                            name
                            favourite
                            decorations {
                                ...decorationContent
                            }
                        }
                    }

                    ${gqlDecorationFragment}
                `,
                {count}
            ).then(data => {
                setProjects(data.lastActiveProjects)
                setTitle(`Last ${count} active projects`)
            })
        }
    }, [client, count, projectsRefreshCount, favouritesRefresh]);

    return (
        <PaddedContent>
            {
                projects && projects.length > 0 &&
                <Space direction="horizontal" size={16} wrap>
                    {
                        projects.map(project => <RowTag key={project.id}>
                                <ProjectBox project={project}/>
                            </RowTag>
                        )
                    }
                </Space>
            }
            {
                (!projects || projects.length === 0) && <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={
                        <Typography.Text>
                            No project has been created in Ontrack yet.
                            You can start <a
                            href="https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#feeding">feeding
                            information</a> in Ontrack
                            automatically from your CI engine, using its API or other means.
                        </Typography.Text>
                    }
                />
            }
        </PaddedContent>
    )
}