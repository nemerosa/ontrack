"use client"

/**
 * A screen the mobile UI does not have yet.
 *
 * The shell ships before the screens it holds - Home, Projects and Search each
 * arrive as their own change. Saying so plainly, and offering the desktop page
 * that does work, beats an empty tab: a user who taps a tab and finds nothing
 * cannot tell a missing feature from a broken one.
 *
 * These are meant to be deleted. A screen landing here replaces this placeholder
 * outright, and adds its route to the map in `@components/mobile/mobileRoutes`.
 */

import {Space, Typography} from "antd"
import DesktopVersionButton from "@components/mobile/DesktopVersionButton"

export default function MobileScreenPending({desktopHref}) {
    return (
        <Space direction="vertical" size="middle" style={{width: '100%'}}>
            <Typography.Text type="secondary" data-testid="mobile-screen-pending">
                This screen is not part of the mobile UI yet.
            </Typography.Text>
            {
                desktopHref &&
                <DesktopVersionButton href={desktopHref} block/>
            }
        </Space>
    )
}
