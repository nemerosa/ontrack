"use client"

/**
 * The bottom navigation bar.
 *
 * Bottom rather than top because that is where a thumb reaches, and a bar rather
 * than a drawer because three destinations do not earn a menu.
 *
 * Deliberately not the desktop `NavBar`: the two layouts share nothing, so that
 * a change to one cannot silently deform the other. See
 * `doc/dev-guide/ui/mobile-ui.md`.
 */

import Link from "next/link"
import {usePathname} from "next/navigation"
import {activeMobileNavKey, MOBILE_NAV_ITEMS} from "@components/mobile/layout/mobileNav"

export default function MobileBottomNav() {

    const pathname = usePathname()
    const active = activeMobileNavKey(pathname)

    return (
        <nav className="ot-mobile-nav" data-testid="mobile-nav" aria-label="Main">
            {
                MOBILE_NAV_ITEMS.map(item =>
                    <Link
                        key={item.key}
                        href={item.href}
                        className="ot-mobile-nav-item"
                        data-testid={`mobile-nav-${item.key}`}
                        // Both the styling hook and what a screen reader
                        // announces as the current page.
                        aria-current={item.key === active ? 'page' : undefined}
                    >
                        {item.icon}
                        <span>{item.label}</span>
                    </Link>
                )
            }
        </nav>
    )
}
