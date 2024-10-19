import SlotEligibleBuild from "@components/extension/environments/SlotEligibleBuild";

export default function SlotEligibleBuildsTable({slot, onChange}) {
    return (
        <>
            <SlotEligibleBuild slot={slot} onStart={onChange}/>
        </>
    )
}