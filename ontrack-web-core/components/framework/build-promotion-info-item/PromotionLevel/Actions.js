import React from "react";
import {isAuthorized} from "@components/common/authorizations";
import BuildPromoteAction from "@components/builds/BuildPromoteAction";

export default function PromotionLevelBuildPromotionInfoItemActions({item, build, promotionLevel, onChange}) {
    return isAuthorized(build, 'build', 'promote') ?
        <BuildPromoteAction
            build={build}
            promotionLevel={item}
            onPromotion={onChange}
        /> : undefined
}