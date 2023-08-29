import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {useState} from "react";
import {Empty, Typography} from "antd";
import {gql} from "graphql-request";
import FavouriteProjectsWidgetForm from "@components/dashboards/widgets/home/FavouriteProjectsWidgetForm";
import {gqlDecorationFragment} from "@components/services/fragments";
import ProjectList from "@components/projects/ProjectList";
import {useDashboardEventForRefresh} from "@components/dashboards/DashboardEventsContext";

export default function FavouriteProjectsWidget() {

    const refreshCount = useDashboardEventForRefresh("project.favourite")

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
                queryDeps={[refreshCount]}
                variables={{}}
                setData={data => setProjects(data.projects)}
                form={<FavouriteProjectsWidgetForm/>}
            >
                <ProjectList projects={projects}/>
                {
                    (!projects || projects.length === 0) && <Empty
                        image={Empty.PRESENTED_IMAGE_SIMPLE}
                        description={
                            <Typography.Text>
                                No project has been selected as a favourite yet.
                            </Typography.Text>
                        }
                    />
                }
            </SimpleWidget>
        </>
    )
}