import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";
import InformationSection from "@components/framework/information/InformationSection";

export default function ValidationStampViewDrawer({validationStamp, loading}) {
    return (
        <>
            <InfoViewDrawer
                title="Validation stamp information"
                tooltip="Displays information about the validation stamp"
                width="33%"
            >
                <PropertiesSection
                    entityType="VALIDATION_STAMP"
                    entityId={validationStamp.id}
                />
                <InformationSection
                    entity={validationStamp}
                    loading={loading}
                />
            </InfoViewDrawer>
        </>
    )
}