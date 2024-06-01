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
                    entityType="PROJECT"
                    entityId={project.id}
                />
                <InformationSection
                    loading={loadingProject}
                    entity={project}
                />
            </InfoViewDrawer>
        </>
    )
}