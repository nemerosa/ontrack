"use client"

/**
 * "Open the desktop version".
 *
 * The remember-then-navigate pair lives in `switchToDesktopUI`, which both this
 * button and the way back share; all this adds is the affordance.
 */

import {Button} from "antd"
import {switchToDesktopUI} from "@components/mobile/desktopPreference"

export default function DesktopVersionButton({href, type = "default", block = false}) {
    return (
        <Button
            type={type}
            block={block}
            onClick={() => switchToDesktopUI(href)}
            data-testid="open-desktop-version"
        >
            Open the desktop version
        </Button>
    )
}
