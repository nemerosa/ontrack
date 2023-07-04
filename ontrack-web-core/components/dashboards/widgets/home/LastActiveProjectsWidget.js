import {useState} from "react";
import {gql} from "graphql-request";
import ProjectBox from "@components/projects/ProjectBox";
import {Space} from "antd";
import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {FaPlus} from "react-icons/fa";
import WidgetCommand from "@components/dashboards/commands/WidgetCommand";

export default function LastActiveProjectsWidget({count}) {

    const [projects, setProjects] = useState([])

    const createProject = () => {
        // TODO
    }

    const getCommands = (projects) => {
        return [
            <WidgetCommand
                title="Create project"
                icon={<FaPlus/>}
                onAction={createProject}
            />
        ]
    }

    return (
        <SimpleWidget
            title={`Last ${count} active projects`}
            query={
                gql`
                    query LastActiveProjects($count: Int! = 10) {
                        lastActiveProjects(count: $count) {
                            id
                            name
                            favourite
                        }
                    }
                `
            }
            variables={{}}
            setData={data => setProjects(data.lastActiveProjects)}
            getCommands={projects => getCommands(projects)}
        >
            <Space direction="horizontal" size={16} wrap>
                {projects.map(project => <ProjectBox key={project.id} project={project}/>)}
            </Space>
        </SimpleWidget>
    )
}