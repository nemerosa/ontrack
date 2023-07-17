import PageSection from "@components/common/PageSection";
import PropertyList from "@components/framework/properties/PropertyList";

export default function PropertiesSection({loading, entity}) {
    return (
        <PageSection
            loading={loading}
            title="Properties"
        >
            <PropertyList properties={entity.properties}/>
        </PageSection>
    )
}