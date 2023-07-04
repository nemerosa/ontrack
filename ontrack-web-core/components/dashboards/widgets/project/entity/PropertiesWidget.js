import {useContext, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardPage";
import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {gql} from "graphql-request";
import {Space} from "antd";
import Property from "@components/properties/Property";

export default function PropertiesWidget() {

    const dashboard = useContext(DashboardContext)
    const [properties, setProperties] = useState([])

    // const check = checkContextIn("Properties", [
    //     "project",
    //     "branch",
    //     "build",
    //     "promotion_level",
    //     "validation_stamp",
    //     "promotion_run",
    //     "validation_run",
    // ])
    // if (check) return check

    return (
        <>
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
                    entityType: dashboard.context.toUpperCase(),
                    entityId: dashboard.contextId,
                }}
                setData={data => setProperties(data.entity.properties)}
            >
                <Space direction="vertical" size={8} style={{width: '100%'}}>
                    {
                        properties && properties.map(property => <Property key={property.type.typeName}
                                                                           property={property}/>)
                    }
                </Space>
            </SimpleWidget>
        </>
    )
}