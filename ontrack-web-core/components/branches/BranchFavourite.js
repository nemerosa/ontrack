import {useContext, useEffect, useState} from "react";
import {EventsContext} from "@components/common/EventsContext";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Favourite from "@components/common/Favourite";

export default function BranchFavourite({branch}) {

    const eventsContext = useContext(EventsContext)
    const [favourite, setFavourite] = useState(branch.favourite)

    useEffect(() => {
        setFavourite(branch.favourite)
    }, [branch])

    const toggleFavourite = async () => {
        if (favourite) {
            return graphQLCall(
                gql`
                    mutation UnsetFavourite(
                        $branchId: Int!,
                    ) {
                        unfavouriteBranch(input: {
                            id: $branchId,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `, {branchId: branch.id}
            ).then(() => {
                setFavourite(false)
                eventsContext.fireEvent("branch.favourite", {id: branch.id, value: false})
            })
        } else {
            return graphQLCall(
                gql`
                    mutation SetFavourite(
                        $branchId: Int!,
                    ) {
                        favouriteBranch(input: {
                            id: $branchId,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `, {branchId: branch.id}
            ).then(() => {
                setFavourite(true)
                eventsContext.fireEvent("branch.favourite", {id: branch.id, value: true})
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