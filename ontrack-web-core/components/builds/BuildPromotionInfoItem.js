import {Dynamic} from "@components/common/Dynamic";

export default function buildPromotionInfoItem({item, build, promotionLevel, onChange}) {
    return ({
        label: <Dynamic
            path={`framework/build-promotion-info-item/${item.__typename}/Name`}
            props={{item, build, promotionLevel, onChange}}
        />,
        dot: <Dynamic
            path={`framework/build-promotion-info-item/${item.__typename}/Dot`}
            props={{item, build, promotionLevel, onChange}}
        />,
        children: <Dynamic
            path={`framework/build-promotion-info-item/${item.__typename}/Actions`}
            props={{item, build, promotionLevel, onChange}}
        />,
    })
}