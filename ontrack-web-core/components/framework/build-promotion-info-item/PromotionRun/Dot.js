import React from "react";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";

export default function PromotionRunBuildPromotionInfoItemDor({item, build, promotionLevel, onChange}) {
    return (
        <PromotionLevelImage promotionLevel={promotionLevel} size={16}/>
    )
}