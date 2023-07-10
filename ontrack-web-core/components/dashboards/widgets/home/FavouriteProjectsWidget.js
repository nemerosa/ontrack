import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {useState} from "react";
import {Space} from "antd";
import ProjectBox from "@components/projects/ProjectBox";
import {gql} from "graphql-request";
import FavouriteProjectsWidgetForm from "@components/dashboards/widgets/home/FavouriteProjectsWidgetForm";
import RowTag from "@components/common/RowTag";

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
                form={<FavouriteProjectsWidgetForm/>}
            >
                <Space direction="horizontal" size={16} wrap>
                    {
                        projects.map(project =>
                            <RowTag>
                                <ProjectBox
                                    key={project.id}
                                    project={project}
                                    displayFavourite={false}
                                />
                            </RowTag>
                        )
                    }
                </Space>
            </SimpleWidget>
        </>
    )
}