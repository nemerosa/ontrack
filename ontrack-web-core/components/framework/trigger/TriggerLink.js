import {Dynamic} from "@components/common/Dynamic";

export default function TriggerLink({triggerData}) {
    return <Dynamic path={`framework/trigger/${triggerData.id}/Link`} props={triggerData?.data}/>
}