import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";
import InformationSection from "@components/framework/information/InformationSection";

export default function ValidationRunViewDrawer({run}) {
    return (
        <>
            <InfoViewDrawer
                title="Validation run information"
                tooltip="Displays information about the validation run"
                width="33%"
            >
                <PropertiesSection
                    entityType="VALIDATION_RUN"
                    entityId={run.id}
                />
                <InformationSection
                    entity={run}
                    loading={false}
                />
            </InfoViewDrawer>
        </>
    )
}