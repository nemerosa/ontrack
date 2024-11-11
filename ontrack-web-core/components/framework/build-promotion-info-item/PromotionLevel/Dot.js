import React from "react";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";

export default function PromotionLevelBuildPromotionInfoItemDor({item, build, promotionLevel, onChange}) {
    return (
        <PromotionLevelImage promotionLevel={item} size={16}/>
    )
}