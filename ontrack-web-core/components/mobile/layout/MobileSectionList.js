"use client"

/**
 * A titled section holding a list, or a line saying the list is empty.
 *
 * The build screen's three sections - promotions, deployments, validations - are
 * each exactly that, and so is anything else that reports on one facet of an
 * entity. Without this they are the same eight lines of ternary three times over,
 * and the empty case is the half that gets forgotten.
 *
 * `MobileEntityGroup` is the same idea for a list that is never empty because
 * its screen says so at the screen level; this one owns the choice.
 */

import MobileSection from "@components/mobile/layout/MobileSection"
import MobileEmpty from "@components/mobile/layout/MobileEmpty"

/**
 * @param {React.ReactNode} title The section's heading.
 * @param {string} [testId] Identifies the section as a whole - which is also
 *   what carries the empty message, so a test can assert on either.
 * @param {boolean} isEmpty Whether there is anything to list.
 * @param {React.ReactNode} empty What to say when there is not.
 * @param {React.ReactNode} children The rows - see `MobileEntityRow`.
 */
export default function MobileSectionList({title, testId, isEmpty, empty, children}) {
    return (
        <MobileSection title={title} testId={testId}>
            {
                isEmpty ?
                    <MobileEmpty description={empty}/> :
                    <ul className="ot-mobile-list">
                        {children}
                    </ul>
            }
        </MobileSection>
    )
}
