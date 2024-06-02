import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";
import {gql} from "graphql-request";

export default function PropertyDeleteButton({entityType, entityId, property}) {

    const client = useGraphQLClient()
    const eventsContext = useContext(EventsContext)

    const deleteProperty = () => {
        client.request(
            gql`
                mutation DeleteProperty(
                    $entityType: ProjectEntityType!,
                    $entityId: Int!,
                    $type: String!,
                ) {
                    deleteGenericProperty(input: {
                        entityType: $entityType,
                        entityId: $entityId,
                        type: $type,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                entityType,
                entityId,
                type: property.type.typeName,
            }
        ).then(() => {
            eventsContext.fireEvent("entity.properties.changed", {entity: {entityType, entityId}})
        })
    }

    return (
        <>
            <InlineConfirmCommand
                title="Deletes this property"
                confirm="Do you really want to delete this property?"
                onConfirm={deleteProperty}
            />
        </>
    )
}