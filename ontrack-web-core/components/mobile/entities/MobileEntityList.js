"use client"

/**
 * A titled list of entities, and one row of it.
 *
 * The mobile UI's one list shape: a name, a line of context under it, and a
 * single trailing action. Everything a phone screen shows - favourites, the
 * project list, a project's branches - is that shape, so it is one component
 * rather than a copy per screen.
 *
 * Not antd's `List`: its paddings and its split lines are sized for a desktop
 * page, and the mobile UI shares no layout component with the desktop one. The
 * markup here is a plain `ul`, which is also what a screen reader wants to hear.
 * The look lives in `app/mobile/mobile.css`.
 */

import Link from "next/link"
import MobileSection from "@components/mobile/layout/MobileSection"

/**
 * @param {string} title The section's heading.
 * @param {string} testId Identifies the section as a whole.
 * @param {React.ReactNode} children The rows - see {@link MobileEntityRow}.
 */
export function MobileEntityGroup({title, testId, children}) {
    return (
        <MobileSection title={title} testId={testId}>
            <ul className="ot-mobile-list">
                {children}
            </ul>
        </MobileSection>
    )
}

/**
 * @param {string} testId Identifies this row.
 * @param {React.ReactNode} name What the entity is called.
 * @param {React.ReactNode} [context] The line under it - what makes the name
 *   unambiguous, such as the project a branch belongs to.
 * @param {React.ReactNode} [action] The trailing control, such as the favourite
 *   star. One at most: a phone row has no space for a toolbar.
 * @param {string} [href] Where tapping the row goes. Only ever a mobile route:
 *   sending a tap to a desktop page would bounce the user through the redirect
 *   and, once the app is installed as a PWA scoped to `/mobile`, out of the app.
 *   A row with no screen behind it yet takes no `href` - a tap that 404s is
 *   worse than a row that does not move.
 */
export function MobileEntityRow({testId, name, context, action, href}) {

    /*
     * The link wraps the text and NOT the whole row: the trailing action is
     * itself a control, and nesting a button inside an anchor is invalid markup
     * that browsers and screen readers then resolve differently. The text block
     * grows to fill the row, so everything left of the star is tappable anyway.
     */
    const text = (
        <>
            <span className="ot-mobile-row-name">{name}</span>
            {
                context &&
                <span className="ot-mobile-row-context">{context}</span>
            }
        </>
    )

    return (
        <li className="ot-mobile-row" data-testid={testId}>
            {
                href ?
                    <Link href={href} className="ot-mobile-row-text ot-mobile-row-link">
                        {text}
                    </Link> :
                    <div className="ot-mobile-row-text">
                        {text}
                    </div>
            }
            {
                action &&
                <div className="ot-mobile-row-action">{action}</div>
            }
        </li>
    )
}
