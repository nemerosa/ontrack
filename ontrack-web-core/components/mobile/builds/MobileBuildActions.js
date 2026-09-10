"use client"

/**
 * The promote and deploy entry points on the build screen.
 *
 * **Gated exactly as the desktop UI gates them**, off the build's own
 * `authorizations`: `build/promote` and `slotPipeline/create`. A user without
 * the right does not see the button rather than seeing one that fails - and the
 * second one is answered `false` on an instance with no environments licence,
 * so the deploy entry point disappears there without this file knowing anything
 * about licences.
 *
 * **What they do, for now.** Promoting (#1724) and deploying (#1725) from the
 * phone are their own issues; this one delivers the entry points and the gating.
 * Until those land the buttons switch this device to the desktop UI on the
 * build's own page, through `switchToDesktopUI` - the same cookie-then-navigate
 * pair the interstitial uses, and the same answer the initiative already gives
 * everywhere it does not cover something yet. The alternative was a button that
 * does nothing, which is worse than one that is honest about where it goes; the
 * caption below says so in words.
 *
 * When #1724 and #1725 land, each `onClick` becomes its dialog and the caption
 * goes. Nothing else here changes.
 */

import {Button, Space, Typography} from "antd"
import {FaRegThumbsUp, FaServer} from "react-icons/fa"
import {isAuthorized} from "@components/common/authorizations"
import {switchToDesktopUI} from "@components/mobile/desktopPreference"
import {desktopBuildUri} from "@components/mobile/mobileRoutes"

export default function MobileBuildActions({build}) {

    const canPromote = isAuthorized(build, 'build', 'promote')
    const canDeploy = isAuthorized(build, 'slotPipeline', 'create')

    // Nothing to show a reader who can do neither - and no empty box either.
    if (!canPromote && !canDeploy) return null

    const openDesktop = () => switchToDesktopUI(desktopBuildUri(build.id))

    return (
        <Space direction="vertical" size="small" style={{width: '100%'}} data-testid="mobile-build-actions">
            <Space.Compact block>
                {
                    canPromote &&
                    <Button
                        block
                        type="primary"
                        icon={<FaRegThumbsUp/>}
                        data-testid="mobile-build-promote"
                        onClick={openDesktop}
                    >
                        Promote
                    </Button>
                }
                {
                    canDeploy &&
                    <Button
                        block
                        icon={<FaServer/>}
                        data-testid="mobile-build-deploy"
                        onClick={openDesktop}
                    >
                        Deploy
                    </Button>
                }
            </Space.Compact>
            <Typography.Text type="secondary" className="ot-mobile-caption">
                These open the desktop version for now.
            </Typography.Text>
        </Space>
    )
}
