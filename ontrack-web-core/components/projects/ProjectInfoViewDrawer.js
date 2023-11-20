import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";
import InformationSection from "@components/framework/information/InformationSection";

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
                <InformationSection
                    loading={loadingProject}
                    entity={project}
                />
            </InfoViewDrawer>
        </>
    )
}