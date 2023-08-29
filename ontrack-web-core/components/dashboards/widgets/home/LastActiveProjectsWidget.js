import {useContext, useState} from "react";
import {gql} from "graphql-request";
import ProjectBox from "@components/projects/ProjectBox";
import {Empty, Space, Typography} from "antd";
import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {FaPlus} from "react-icons/fa";
import WidgetCommand from "@components/dashboards/commands/WidgetCommand";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";
import {UserContext} from "@components/providers/UserProvider";
import LastActiveProjectsWidgetForm from "@components/dashboards/widgets/home/LastActiveProjectsWidgetForm";
import RowTag from "@components/common/RowTag";
import {gqlDecorationFragment} from "@components/services/fragments";

export default function LastActiveProjectsWidget({count}) {

    const user = useContext(UserContext)

    const [projects, setProjects] = useState([])
    const [projectsRefreshCount, setProjectsRefreshCount] = useState(0)

    const newProjectDialog = useNewProjectDialog({
        onSuccess: () => {
            setProjectsRefreshCount(projectsRefreshCount + 1)
        }
    })

    const createProject = () => {
        newProjectDialog.start()
    }

    const getCommands = (/*projects*/) => {
        return [
            <WidgetCommand
                key="project-create"
                condition={user.authorizations.project?.create}
                title="Create project"
                icon={<FaPlus/>}
                onAction={createProject}
            />
        ]
    }

    return (
        <>
            <NewProjectDialog newProjectDialog={newProjectDialog}/>
            <SimpleWidget
                title={`Last ${count} active projects`}
                query={
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
                    `
                }
                queryDeps={[user, count, projectsRefreshCount]}
                variables={{count}}
                setData={data => setProjects(data.lastActiveProjects)}
                getCommands={projects => getCommands(projects)}
                form={<LastActiveProjectsWidgetForm count={count}/>}
            >
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
                    (!projects || projects.length == 0) && <Empty
                        image={Empty.PRESENTED_IMAGE_SIMPLE}
                        description={
                            <Typography.Text>
                                No project has been created in Ontrack yet.
                                You can start <a href="https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#feeding">feeding information</a> in Ontrack
                                automatically from your CI engine, using its API or other means.
                                Or you can <a onClick={createProject}>create a project</a> using the UI.
                            </Typography.Text>
                        }
                    />
                }
            </SimpleWidget>
        </>
    )
}