import {Dynamic} from "@components/common/Dynamic";

export default function TriggerComponent({triggerData}) {
    return <Dynamic path={`framework/trigger/${triggerData.id}/Component`} props={triggerData?.data}/>
}