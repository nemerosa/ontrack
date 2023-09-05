import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";

export default function BuildInfoViewDrawer({build, loading}) {
    return (
        <>
            <InfoViewDrawer
                title="Build information"
                tooltip="Displays information about the build"
                width="33%"
            >
                <PropertiesSection
                    entity={build}
                    loading={loading}
                />
                {/*  TODO Extra information  */}
            </InfoViewDrawer>
        </>
    )
}