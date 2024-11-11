import {Typography} from "antd";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelineBuildPromotionInfoItemActions({item, build, promotionLevel, onChange}) {
    return (
        <>
            <Typography.Link type="secondary" href={slotPipelineUri(item.id)}>
                {item.slot.environment.name}{item.slot.qualifier && `/${item.slot.qualifier}`} #{item.number}
            </Typography.Link>
        </>
    )
}