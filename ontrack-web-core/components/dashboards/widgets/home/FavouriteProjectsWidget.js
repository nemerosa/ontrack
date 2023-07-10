import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {useState} from "react";
import {Space} from "antd";
import ProjectBox from "@components/projects/ProjectBox";
import {gql} from "graphql-request";
import FavouriteProjectsWidgetForm from "@components/dashboards/widgets/home/FavouriteProjectsWidgetForm";
import RowTag from "@components/common/RowTag";
import {gqlDecorationFragment} from "@components/services/fragments";
import ProjectList from "@components/projects/ProjectList";

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
                                decorations {
                                  ...decorationContent
                                }
                                branches(useModel: true, count: 10) {
                                    id
                                    name
                                    latestBuild: builds(count: 1) {
                                      id
                                      name
                                    }
                                    promotionLevels {
                                      id
                                      name
                                      image
                                      promotionRuns(first: 1) {
                                        build {
                                          id
                                          name
                                        }
                                      }
                                    }
                                }
                            }
                        }
                        ${gqlDecorationFragment}
                    `
                }
                variables={{}}
                setData={data => setProjects(data.projects)}
                form={<FavouriteProjectsWidgetForm/>}
            >
                <ProjectList projects={projects}/>
            </SimpleWidget>
        </>
    )
}