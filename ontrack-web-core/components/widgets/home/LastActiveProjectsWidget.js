import {useContext, useEffect, useState} from "react";
import RowTag from "@components/common/RowTag";
import ProjectBox from "@components/projects/ProjectBox";
import {Empty, Space, Typography} from "antd";
import {useEventForRefresh} from "@components/common/EventsContext";
import {UserContext} from "@components/providers/UserProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {FaPlus} from "react-icons/fa";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";

import GridCellCommand from "@components/grid/GridCellCommand";

export default function LastActiveProjectsWidget({count}) {

    const user = useContext(UserContext)
    const client = useGraphQLClient()
    const [projects, setProjects] = useState([])
    const [projectsRefreshCount, setProjectsRefreshCount] = useState(0)
    const favouritesRefresh = useEventForRefresh("project.favourite")

    const {setTitle, setExtra} = useContext(DashboardWidgetCellContext)

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

    const newProjectDialog = useNewProjectDialog({
        onSuccess: () => {
            setProjectsRefreshCount(projectsRefreshCount + 1)
        }
    })

    const createProject = () => {
        newProjectDialog.start()
    }

    useEffect(() => {
        setExtra(
            <>
                <GridCellCommand
                    key="project-create"
                    condition={user.authorizations.project?.create}
                    title="Create project"
                    icon={<FaPlus/>}
                    onAction={createProject}
                />
            </>
        )
    }, [user])

    return (
        <>
            <NewProjectDialog newProjectDialog={newProjectDialog}/>
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
                            Or you can <a onClick={createProject}>create a project</a> using the UI.
                        </Typography.Text>
                    }
                />
            }
        </>
    )
}