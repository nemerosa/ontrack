import {useContext, useEffect, useState} from "react";
import {Empty, Typography} from "antd";
import {gql} from "graphql-request";
import {gqlDecorationFragment} from "@components/services/fragments";
import ProjectList from "@components/projects/ProjectList";
import {useEventForRefresh} from "@components/common/EventsContext";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function FavouriteProjectsWidget() {

    const client = useGraphQLClient()
    const refreshCount = useEventForRefresh("project.favourite")

    const [projects, setProjects] = useState([])
    useEffect(() => {
        if (client) {
            client.request(
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
            ).then(data => {
                setProjects(data.projects)
            })
        }
    }, [client, refreshCount]);

    const {setTitle} = useContext(DashboardWidgetCellContext)
    useEffect(() => {
        setTitle("Favourite projects")
    }, [])

    return (
        <>
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
        </>
    )
}