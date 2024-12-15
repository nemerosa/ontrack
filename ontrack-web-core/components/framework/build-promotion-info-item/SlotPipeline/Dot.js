import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";

export default function SlotPipelineBuildPromotionInfoItemDot({item}) {
    return <EnvironmentIcon environmentId={item.slot.environment.id}/>
}