import InfoViewDrawer from "@components/common/InfoViewDrawer";
import PropertiesSection from "@components/framework/properties/PropertiesSection";
import InformationSection from "@components/framework/information/InformationSection";

export default function BranchInfoViewDrawer({branch, loadingBranch}) {
    return (
        <>
            <InfoViewDrawer
                title="Branch information"
                tooltip="Displays information about the branch"
                width="40%"
            >
                <PropertiesSection
                    entityType="BRANCH"
                    entityId={branch.id}
                />
                <InformationSection
                    entity={branch}
                    loading={loadingBranch}
                />
            </InfoViewDrawer>
        </>
    )
}