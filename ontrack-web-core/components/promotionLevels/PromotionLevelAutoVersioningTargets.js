import AutoVersioningPromotionLevelTrail from "@components/extension/auto-versioning/AutoVersioningPromotionLevelTrail";

export default function PromotionLevelAutoVersioningTargets({promotionLevel}) {
    return (
        <div data-testid="auto-versioning-trail">
            <AutoVersioningPromotionLevelTrail promotionLevelId={promotionLevel.id}/>
        </div>
    )
}