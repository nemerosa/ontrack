import {Space} from "antd";
import Property from "@components/framework/properties/Property";

export default function PropertyList({properties}) {
    return (
        <>
            {
                properties && <Space direction="vertical">
                    {properties.filter(it => it.value).map(property =>
                        <Property key={property.type.typeName} property={property}/>
                    )}
                </Space>
            }
        </>
    )
}