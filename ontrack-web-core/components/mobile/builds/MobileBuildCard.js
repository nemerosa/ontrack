"use client"

/**
 * One build, as a card.
 *
 * This is the mobile UI's one real divergence from the desktop layout rather
 * than a smaller copy of it. `BranchBuilds` renders builds as a matrix with a
 * column per validation stamp: it is wide by construction, and no amount of
 * narrowing turns a matrix into something readable at 375px. A phone gets a card
 * per build instead - what it is called, when it happened, how far it has been
 * promoted, and where it is deployed.
 *
 * Validation *status* is deliberately absent: per-stamp status is the build
 * screen's job, and a strip of validation chips here would rebuild the matrix
 * one card at a time.
 *
 * The card is not a link. The build screen is its own issue; until it exists, a
 * tap that 404s is worse than a card that does not move.
 */

import {FaServer} from "react-icons/fa"
import {Typography} from "antd"
import TimestampText from "@components/common/TimestampText"
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage"

/**
 * The size of a promotion medal on a card.
 *
 * Bigger than the desktop's 16px: on a phone that is a coloured dot. It is
 * paired with the level's name for the same reason - the acceptance criterion is
 * that promotions read without zooming, and only text does that reliably.
 */
const MEDAL_SIZE = 20

/**
 * @param {Object} build A build with `displayName`, `creation`, `promotionRuns`
 *   and `currentDeployments`.
 */
export default function MobileBuildCard({build}) {

    const promotions = build.promotionRuns ?? []
    const deployments = build.currentDeployments ?? []

    return (
        <li className="ot-mobile-card" data-testid={`mobile-build-${build.id}`}>
            <div className="ot-mobile-card-head">
                <span className="ot-mobile-card-title">{build.displayName || build.name}</span>
                <span className="ot-mobile-card-time" data-testid={`mobile-build-${build.id}-time`}>
                    {/*
                      Relative, with the absolute time one tap away: an age is
                      what a scanning reader wants ("did this run today?"), and
                      it is also much shorter, which matters on a row that has to
                      share its line with the build's name.
                    */}
                    <TimestampText value={build.creation?.time} relative/>
                </span>
            </div>
            {
                promotions.length > 0 &&
                <div className="ot-mobile-badges" data-testid={`mobile-build-${build.id}-promotions`}>
                    {
                        promotions.map(run =>
                            <span
                                key={run.id}
                                className="ot-mobile-badge"
                                data-testid={`mobile-promotion-${run.id}`}
                            >
                                <PromotionLevelImage
                                    promotionLevel={run.promotionLevel}
                                    size={MEDAL_SIZE}
                                />
                                <span className="ot-mobile-badge-text">{run.promotionLevel?.name}</span>
                            </span>
                        )
                    }
                </div>
            }
            {
                deployments.length > 0 &&
                <div className="ot-mobile-badges" data-testid={`mobile-build-${build.id}-deployments`}>
                    {
                        deployments.map(pipeline =>
                            <span
                                key={pipeline.id}
                                className="ot-mobile-badge"
                                data-testid={`mobile-deployment-${pipeline.id}`}
                            >
                                {/*
                                  The environments icon the desktop UI uses. It
                                  is what tells this strip from the promotions
                                  one above it - by shape rather than by colour,
                                  which the light theme's identical page and
                                  elevated surfaces rule out.
                                */}
                                <span className="ot-mobile-badge-icon" aria-hidden="true">
                                    <FaServer/>
                                </span>
                                <span className="ot-mobile-badge-text">
                                    {pipeline.slot?.environment?.name}
                                </span>
                                {
                                    /*
                                     * A project can hold several slots in the
                                     * same environment, told apart only by their
                                     * qualifier. Without it two deployments show
                                     * as the same badge twice.
                                     */
                                    pipeline.slot?.qualifier &&
                                    <Typography.Text type="secondary" className="ot-mobile-badge-qualifier">
                                        {pipeline.slot.qualifier}
                                    </Typography.Text>
                                }
                            </span>
                        )
                    }
                </div>
            }
        </li>
    )
}
