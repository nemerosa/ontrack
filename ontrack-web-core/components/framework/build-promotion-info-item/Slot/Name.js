import {Typography} from "antd";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotBuildPromotionInfoItemName({item, build, onChange}) {
    return (
        <>
            <Typography.Link type="secondary" href={slotUri(item)}>
                {item.environment.name}{item.qualifier && `/${item.qualifier}`}
            </Typography.Link>
        </>
    )
}