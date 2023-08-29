import {Space, Typography} from "antd";
import Favourite from "@components/common/Favourite";
import {projectLink} from "@components/common/Links";
import Decorations from "@components/framework/decorations/Decorations";
import {useContext, useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {DashboardEventsContext} from "@components/dashboards/DashboardEventsContext";

export default function ProjectBox({project, displayFavourite = true, displayDecorations = true}) {

    const dashboardEventsContext = useContext(DashboardEventsContext)
    const [favourite, setFavourite] = useState(project.favourite)

    useEffect(() => {
        setFavourite(project.favourite)
    }, [project])

    const toggleFavourite = async () => {
        if (favourite) {
            return graphQLCall(
                gql`
                    mutation UnsetFavourite(
                        $projectId: Int!,
                    ) {
                        unfavouriteProject(input: {
                            id: $projectId,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `, {projectId: project.id}
            ).then(() => {
                setFavourite(false)
                dashboardEventsContext.fireEvent("project.favourite", {id: project.id, value: false})
            })
        } else {
            return graphQLCall(
                gql`
                    mutation SetFavourite(
                        $projectId: Int!,
                    ) {
                        favouriteProject(input: {
                            id: $projectId,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `, {projectId: project.id}
            ).then(() => {
                setFavourite(true)
                dashboardEventsContext.fireEvent("project.favourite", {id: project.id, value: true})
            })
        }
    }

    return (
        <>
            <Space>
                {displayFavourite ? <Favourite
                    value={favourite}
                    onToggle={toggleFavourite}
                /> : undefined}
                {
                    projectLink(project, <Typography.Text strong>{project.name}</Typography.Text>)
                }
                {
                    displayDecorations && <Decorations entity={project}/>
                }
            </Space>
        </>
    )
}