import {useContext, useEffect, useState} from "react";
import {EventsContext} from "@components/common/EventsContext";
import {gql} from "graphql-request";
import Favourite from "@components/common/Favourite";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function BranchFavourite({branch}) {

    const eventsContext = useContext(EventsContext)
    const [favourite, setFavourite] = useState(branch.favourite)

    useEffect(() => {
        setFavourite(branch.favourite)
    }, [branch])

    const client = useGraphQLClient()

    const toggleFavourite = async () => {
        if (favourite) {
            return client.request(
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
            return client.request(
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