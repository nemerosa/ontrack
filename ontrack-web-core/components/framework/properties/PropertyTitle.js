import {Space, Typography} from "antd";
import PropertyEditButton from "@components/core/model/properties/PropertyEditButton";
import PropertyDeleteButton from "@components/core/model/properties/PropertyDeleteButton";

export default function PropertyTitle({entityType, entityId, property}) {
    return (
        <>
            <Space>
                <Typography.Text>{property.type.name}</Typography.Text>
                {
                    property.editable && <>
                        <PropertyEditButton
                            entityType={entityType}
                            entityId={entityId}
                            property={property}
                        />
                        <PropertyDeleteButton
                            entityType={entityType}
                            entityId={entityId}
                            property={property}
                        />
                    </>
                }
            </Space>
        </>
    )
}