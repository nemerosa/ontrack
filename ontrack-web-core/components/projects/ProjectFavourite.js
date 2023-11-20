import {useContext, useEffect, useState} from "react";
import {EventsContext} from "@components/common/EventsContext";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Favourite from "@components/common/Favourite";

export default function ProjectFavourite({project}) {

    const eventsContext = useContext(EventsContext)
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
                eventsContext.fireEvent("project.favourite", {id: project.id, value: false})
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
                eventsContext.fireEvent("project.favourite", {id: project.id, value: true})
            })
        }
    }

    return (
        <>
            <Favourite
                value={favourite}
                onToggle={toggleFavourite}
            />
        </>
    )
}