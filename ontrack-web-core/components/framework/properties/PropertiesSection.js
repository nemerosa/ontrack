import ListSection from "@components/common/ListSection";
import {FaWrench} from "react-icons/fa";
import PropertyAddButton from "@components/core/model/properties/PropertyAddButton";
import PropertyIcon from "@components/framework/properties/PropertyIcon";
import PropertyComponent from "@components/framework/properties/PropertyComponent";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useEventForRefresh} from "@components/common/EventsContext";

export default function PropertiesSection({entityType, entityId}) {

    const client = useGraphQLClient()
    const [propertyList, setPropertyList] = useState([])

    const refreshCount = useEventForRefresh("entity.properties.changed")

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query EntityProperties(
                        $type: ProjectEntityType!,
                        $id: Int!,
                    ) {
                        entity(type: $type, id: $id) {
                            properties {
                                editable
                                type {
                                    typeName
                                    name
                                    description
                                }
                                value
                            }
                        }
                    }
                `,
                {
                    type: entityType,
                    id: entityId,
                }
            ).then(data => {
                setPropertyList(data.entity.properties)
            })
        }
    }, [client, entityType, entityId, refreshCount])

    return (
        <ListSection
            icon={<FaWrench/>}
            title="Properties"
            extraTitle={
                <>
                    <PropertyAddButton entityType={entityType} entityId={entityId} propertyList={propertyList}/>
                </>
            }
            items={
                propertyList
                    .filter(it => it.value)
                    .map(property => {
                        return {
                            title: property.type.name,
                            icon: <PropertyIcon property={property}/>,
                            content: <PropertyComponent property={property}/>,
                        }
                    })
            }
        />
    )
}