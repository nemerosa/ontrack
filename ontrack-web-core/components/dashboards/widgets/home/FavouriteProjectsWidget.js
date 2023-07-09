import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {useState} from "react";
import {Space} from "antd";
import ProjectBox from "@components/projects/ProjectBox";
import {gql} from "graphql-request";

export default function FavouriteProjectsWidget() {

    const [projects, setProjects] = useState([])

    return (
        <>
            <SimpleWidget
                title="Favourite projects"
                query={
                    gql`
                        query FavouriteProjects {
                            projects(favourites: true) {
                                id
                                name
                                favourite
                            }
                        }
                    `
                }
                variables={{}}
                setData={data => setProjects(data.projects)}
            >
                <Space direction="horizontal" size={16} wrap>
                    {
                        projects.map(project =>
                            <ProjectBox
                                key={project.id}
                                project={project}
                                displayFavourite={false}
                            />
                        )
                    }
                </Space>
            </SimpleWidget>
        </>
    )
}