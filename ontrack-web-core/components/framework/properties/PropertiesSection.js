import ListSection from "@components/common/ListSection";
import Property from "@components/framework/properties/Property";

export default function PropertiesSection({loading, entity}) {
    return (
        <ListSection
            title="Properties"
            loading={loading}
            items={entity.properties.filter(it => it.value)}
            renderItem={(property) => (
                <Property property={property}/>
            )}
        />
    )
}