import {Dynamic} from "@components/common/Dynamic";

/**
 * Used in a Steps component.
 */
export default function buildPromotionInfoItem({item, build, promotionLevel, onChange}) {
    return ({
        icon: <Dynamic
            path={`framework/build-promotion-info-item/${item.__typename}/Dot`}
            props={{item, build, promotionLevel, onChange}}
        />,
        status: 'finish',
    })
}