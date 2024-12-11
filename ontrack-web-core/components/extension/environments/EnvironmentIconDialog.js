import ChangeImageDialog, {useChangeImageDialog} from "@components/common/ChangeImageDialog";
import {gql} from "graphql-request";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import {useRestClient} from "@components/providers/ConnectionContextProvider";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";

export const useEnvironmentIconDialog = () => {

    const restClient = useRestClient()
    const eventsContext = useContext(EventsContext)

    return useChangeImageDialog({
        query: gql`
            query Environment($id: String!) {
                environmentById(id: $id) {
                    id
                    name
                    order
                    image
                }
            }
        `,
        queryUserNode: 'environmentById',
        imageCallback: (data, id) => {
            restClient.put(`/rest/extension/environments/environments/${id}/image`, data).then(() => {
                eventsContext.fireEvent("environment.image", {id})
            })
        }
    })
}

export default function EnvironmentIconDialog({dialog}) {
    return (
        <>
            <ChangeImageDialog
                changeImageDialog={dialog}
                renderer={environment => <EnvironmentIcon environmentId={environment.id}/>}
            />
        </>
    )
}