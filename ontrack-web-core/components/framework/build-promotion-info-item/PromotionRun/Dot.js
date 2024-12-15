import React from "react";
import PromotionRunStep from "@components/promotionRuns/PromotionRunStep";

export default function PromotionRunBuildPromotionInfoItemDor({item, build, promotionLevel, onChange}) {
    return (
        <PromotionRunStep
            run={{
                ...item,
                build,
                promotionLevel,
            }}
            onChange={onChange}
        />
    )
}