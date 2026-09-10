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
 */

import Link from "next/link"
import {useContext} from "react"
import {UserContext} from "@components/providers/UserProvider"
import {MOBILE_HOME} from "@components/mobile/mobileRoutes"

export default function MobileHeader() {

    const user = useContext(UserContext)

    return (
        <header className="ot-mobile-header" data-testid="mobile-header">
            <Link href={MOBILE_HOME} className="ot-mobile-brand">
                Yontrack
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
