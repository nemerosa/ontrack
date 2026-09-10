"use client"

/**
 * The mobile UI's header.
 *
 * Compact on purpose: on a phone every row it takes is a row the content does
 * not get. It carries the identity - which is also the way back to the home
 * screen - and who is signed in, which on a shared or long-lived phone session
 * is the one thing worth the space.
 *
 * Everything the desktop header offers behind the user menu - administration,
 * configurations, GraphiQL - has no mobile counterpart and is not smuggled in
 * here.
 *
 * The identity is the brand's own two marks, `yontrack-logo.svg` and
 * `yontrack-text.svg`, and not the word set in the UI font: the wordmark is a
 * drawn typeface in brand lilac, and typing "Yontrack" instead loses both. Both
 * marks carry their own colours - brand green and brand lilac - which read on
 * the header's purple in either theme, because the header is that purple in
 * both.
 */

import Link from "next/link"
import Image from "next/image"
import {useContext} from "react"
import {UserContext} from "@components/providers/UserProvider"
import {MOBILE_HOME} from "@components/mobile/mobileRoutes"

/*
 * Each mark's own aspect ratio, at the height the header gives it. Next compares
 * what it renders against these and warns when the two disagree - which is what
 * the desktop `NavBar` does by drawing the 8.08:1 wordmark at 120x24, a 5:1 box
 * that squashes it.
 *
 * The wordmark is set shorter than the mark is tall: at the mark's 24px it would
 * be 194px wide, half of a 375px header.
 */
const LOGO = {width: 27, height: 24}     // 126.52 x 112.13
const WORDMARK = {width: 129, height: 16} // 461.9 x 57.19

export default function MobileHeader() {

    const user = useContext(UserContext)

    return (
        <header className="ot-mobile-header" data-testid="mobile-header">
            <Link href={MOBILE_HOME} className="ot-mobile-brand" data-testid="mobile-brand">
                <Image
                    src="/yontrack-logo.svg"
                    // Empty on purpose: the wordmark beside it carries the name,
                    // so this one is decorative - an alt text here would have
                    // the link announce itself twice.
                    alt=""
                    width={LOGO.width}
                    height={LOGO.height}
                    className="ot-mobile-logo"
                    data-testid="mobile-logo"
                />
                <Image
                    // The name, drawn rather than set, so this is the one that
                    // carries it for a screen reader.
                    src="/yontrack-text.svg"
                    alt="Yontrack"
                    width={WORDMARK.width}
                    height={WORDMARK.height}
                    className="ot-mobile-wordmark"
                    data-testid="mobile-wordmark"
                />
            </Link>
            {
                user?.fullName || user?.name ?
                    <span className="ot-mobile-user" data-testid="mobile-user">
                        {user.fullName || user.name}
                    </span> : undefined
            }
        </header>
    )
}
