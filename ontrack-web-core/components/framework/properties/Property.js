import {Space, Typography} from "antd";
import Section from "@components/common/Section";
import useDynamic from "@components/common/Dynamic";

export default function Property({property}) {

    const shortTypeName = property.type.typeName.slice("net.nemerosa.ontrack.extension.".length)

    const propertyIconComponent = useDynamic({
        path: `framework/properties/${shortTypeName}/Icon`,
        errorMessage: `Cannot load icon for property ${shortTypeName}`,
        props: {}
    })

    const propertyComponent = useDynamic({
        path: `framework/properties/${shortTypeName}/Display`,
        errorMessage: `Cannot load component for property ${shortTypeName}`,
        props: {property}
    })

    return (
        <Section title={
            <Space>
                {propertyIconComponent}
                <Typography.Text strong>{property.type.name}</Typography.Text>
            </Space>
        }>
            {propertyComponent}
        </Section>
    )
}