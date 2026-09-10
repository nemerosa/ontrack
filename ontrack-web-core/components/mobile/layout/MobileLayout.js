"use client"

/**
 * The mobile shell: a fixed header, the screen, and the bottom bar.
 *
 * Applied by `app/mobile/layout.js` to every mobile screen, the interstitial
 * included - a user who lands there still needs a way out that is not the back
 * button.
 *
 * Shares no component with `MainLayout`. That is the boundary the mobile UI is
 * built on: services, GraphQL fragments and mutations are shared, layout is not.
 */

import MobileHeader from "@components/mobile/layout/MobileHeader"
import MobileBottomNav from "@components/mobile/layout/MobileBottomNav"

export default function MobileLayout({children}) {
    return (
        <>
            <MobileHeader/>
            <main className="ot-mobile-main" data-testid="mobile-main">
                {children}
            </main>
            <MobileBottomNav/>
        </>
    )
}
