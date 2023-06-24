import {checkContextIn} from "@components/dashboards/widgets/Widget";
import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {gql} from "graphql-request";
import {useState} from "react";
import {Space} from "antd";
import Property from "@components/properties/Property";

export default function PropertiesWidget({context, contextId}) {

    const [properties, setProperties] = useState([])

    const check = checkContextIn("Properties", context, [
        "project",
        "branch",
        "build",
        "promotion_level",
        "validation_stamp",
        "promotion_run",
        "validation_run",
    ])
    if (check) return check

    const entityType = context.toUpperCase()

    return (
        <SimpleWidget
            title="Properties"
            query={
            gql`
                query ProjectEntity(
                    $entityType: ProjectEntityType!,
                    $entityId: Int!,
                ) {
                    entity( type: $entityType, id: $entityId) {
                        properties(hasValue: true) {
                            type {
                                typeName
                                name         
                                description                   
                            }
                            editable
                            value
                        }
                    }
                }
            `
            }
            variables={{
                entityType,
                entityId: contextId,
            }}
            setData={data => setProperties(data.entity.properties)}
        >
            <Space direction="vertical" size={8} style={{width: '100%'}}>
                {
                    properties && properties.map(property => <Property key={property.type.typeName} property={property}/>)
                }
            </Space>
        </SimpleWidget>
    )
}