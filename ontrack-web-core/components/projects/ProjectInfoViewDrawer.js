import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";

export default function ProjectInfoViewDrawer({project, loadingProject}) {
    return (
        <>
            <InfoViewDrawer
                title="Project information"
                tooltip="Displays information about the project"
                width="40%"
            >
                <PropertiesSection
                    loading={loadingProject}
                    entity={project}
                />
                {/*  TODO Extra information  */}
            </InfoViewDrawer>
        </>
    )
}