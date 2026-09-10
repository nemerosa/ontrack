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

import {Typography} from "antd"

/**
 * @param {string} title The section's heading.
 * @param {string} testId Identifies the section as a whole.
 * @param {React.ReactNode} children The rows - see {@link MobileEntityRow}.
 */
export function MobileEntityGroup({title, testId, children}) {
    return (
        <section className="ot-mobile-group" data-testid={testId}>
            <Typography.Title level={5} className="ot-mobile-group-title">
                {title}
            </Typography.Title>
            <ul className="ot-mobile-list">
                {children}
            </ul>
        </section>
    )
}

/**
 * @param {string} testId Identifies this row.
 * @param {React.ReactNode} name What the entity is called.
 * @param {React.ReactNode} [context] The line under it - what makes the name
 *   unambiguous, such as the project a branch belongs to.
 * @param {React.ReactNode} [action] The trailing control, such as the favourite
 *   star. One at most: a phone row has no space for a toolbar.
 */
export function MobileEntityRow({testId, name, context, action}) {
    return (
        <li className="ot-mobile-row" data-testid={testId}>
            <div className="ot-mobile-row-text">
                <span className="ot-mobile-row-name">{name}</span>
                {
                    context &&
                    <span className="ot-mobile-row-context">{context}</span>
                }
            </div>
            {
                action &&
                <div className="ot-mobile-row-action">{action}</div>
            }
        </li>
    )
}
