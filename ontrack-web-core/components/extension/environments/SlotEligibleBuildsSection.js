import PageSection from "@components/common/PageSection";
import SlotBuildEligibilitySwitch from "@components/extension/environments/SlotBuildEligibilitySwitch";
import SlotEligibleBuildsTable from "@components/extension/environments/SlotEligibleBuildsTable";
import {useState} from "react";

export default function SlotEligibleBuildsSection({slot, onChange}) {

    const [showEligibleBuilds, setShowEligibleBuilds] = useState(false)

    return (
        <>
            <PageSection
                title="Builds"
                padding={false}
                extra={
                    <SlotBuildEligibilitySwitch
                        value={showEligibleBuilds}
                        onChange={setShowEligibleBuilds}
                    />
                }
            >
                <SlotEligibleBuildsTable
                    slot={slot}
                    onChange={onChange}
                    showEligibleBuilds={showEligibleBuilds}
                />
            </PageSection>
        </>
    )
}