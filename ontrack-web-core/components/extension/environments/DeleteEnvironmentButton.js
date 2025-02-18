import {UserContext} from "@components/providers/UserProvider";
import {useContext, useState} from "react";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {EventsContext} from "@components/common/EventsContext";

export default function DeleteEnvironmentButton({environment}) {

    const eventsContext = useContext(EventsContext)
    const user = useContext(UserContext)
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)

    const deleteEnvironment = () => {
        setLoading(true)
        client.request(
            gql`
                mutation DeleteEnvironment(
                    $id: String!,
                ) {
                    deleteEnvironment(input: {
                        id: $id
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                id: environment.id,
            }
        ).then(() => {
            eventsContext.fireEvent("environment.deleted", {id: environment.id})
        }).finally(() => {
            setLoading(false)
        })
    }

    return (
        <>
            {
                user.authorizations.environment?.create &&
                <>
                    <InlineConfirmCommand
                        title="Environment deletion"
                        confirm={`Do you really want to delete the ${environment.name}? This will remove all data associated with it.`}
                        onConfirm={deleteEnvironment}
                        loading={loading}
                    />
                </>
            }
        </>
    )
}