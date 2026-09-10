"use client"

/**
 * "There is nothing here", in the mobile UI's one voice.
 *
 * Every mobile screen has at least one of these - no project, no branch, no
 * build, no promotion - and they were each a hand-rolled `Empty` with the same
 * two props. One component so a change of voice is one edit, and so nobody has
 * to remember which of antd's images the mobile UI uses.
 *
 * `PRESENTED_IMAGE_SIMPLE` and not the default: the default is a large
 * illustration sized for a desktop page, and these sit inside sections a phone
 * screen stacks several of.
 *
 * The richer empty states - `MobileFavouritesEmpty`, which explains what
 * favourites are and links to the project list - are their own components. This
 * is for the one-line kind.
 */

import {Empty} from "antd"

/**
 * @param {React.ReactNode} description What is missing, as a sentence.
 * @param {string} [testId]
 */
export default function MobileEmpty({description, testId}) {
    return (
        <div data-testid={testId}>
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={description}/>
        </div>
    )
}
