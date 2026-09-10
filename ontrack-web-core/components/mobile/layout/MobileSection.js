"use client"

/**
 * A titled block of a mobile screen.
 *
 * The heading half of `MobileEntityGroup`, on its own, for the screens whose
 * sections are not all lists: the build screen's promotions, deployments and
 * validations are each a list *or* a line saying there is none, and both have to
 * sit under the same heading, spaced the same way as every other section.
 *
 * `MobileEntityGroup` is this plus the list, so the two cannot drift apart.
 */

import {Typography} from "antd"

/**
 * @param {React.ReactNode} title The section's heading.
 * @param {string} [testId] Identifies the section as a whole.
 * @param {React.ReactNode} children
 */
export default function MobileSection({title, testId, children}) {
    return (
        <section className="ot-mobile-group" data-testid={testId}>
            <Typography.Title level={5} className="ot-mobile-group-title">
                {title}
            </Typography.Title>
            {children}
        </section>
    )
}
