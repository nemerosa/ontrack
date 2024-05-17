import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";
import InformationSection from "@components/framework/information/InformationSection";

export default function PromotionLevelViewDrawer({promotionLevel, loading}) {
    return (
        <>
            <InfoViewDrawer
                title="Promotion level information"
                tooltip="Displays information about the promotion level"
                width="33%"
            >
                <PropertiesSection
                    entity={promotionLevel}
                    loading={loading}
                />
                <InformationSection
                    entity={promotionLevel}
                    loading={loading}
                />
            </InfoViewDrawer>
        </>
    )
}