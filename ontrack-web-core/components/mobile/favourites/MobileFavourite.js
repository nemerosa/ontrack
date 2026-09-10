"use client"

/**
 * The favourite star, for a thumb.
 *
 * Deliberately not the desktop `Favourite`: that one is a 14px icon inside a
 * `Typography.Text` with a click handler, which is neither a 44px touch target
 * nor anything a screen reader announces as a control. Same four mutations, own
 * affordance - the boundary the mobile UI is built on.
 *
 * **Controlled.** The favourite state lives in the list that renders the toggle,
 * because that list is what the toggle changes: unstarring on the home screen
 * removes the row, starring on the project list has to survive a re-render. A
 * copy held here would be a second source of truth for the same fact. The only
 * state the toggle owns is whether its own call is in flight.
 *
 * @param {string} type `project` or `branch` - see `favouriteMutations`.
 * @param {number} id The entity's id.
 * @param {string} name What the entity is called, so the control says which one
 *   it acts on rather than announcing a row of identical stars.
 * @param {boolean} favourite Whether the entity is a favourite *now*.
 * @param {function(boolean)} [onToggled] Called with the new state, once the
 *   server has actually reached it.
 */

import {useState} from "react"
import {FaRegStar, FaStar} from "react-icons/fa"
import {Spin} from "antd"
import {callGraphQL} from "@components/services/GraphQL"
import {useMessageApi} from "@components/providers/MessageProvider"
import {favouriteMutation} from "@components/mobile/favourites/favouriteMutations"

export default function MobileFavourite({type, id, name, favourite, onToggled}) {

    const messageApi = useMessageApi()
    const [running, setRunning] = useState(false)

    const onClick = async () => {
        if (running) return
        setRunning(true)
        const target = !favourite
        try {
            const {query, userNode} = favouriteMutation(type, target)
            // `Number`, because a GraphQL `ID` comes back as a string and the
            // mutations take an `Int!` - which rejects `"286"` outright.
            const data = await callGraphQL({query, variables: {id: Number(id)}})
            const errors = data?.[userNode]?.errors
            if (errors && errors.length > 0) {
                messageApi?.error(errors[0].message)
            } else if (onToggled) {
                onToggled(target)
            }
        } catch (error) {
            messageApi?.error(error.message)
        } finally {
            setRunning(false)
        }
    }

    return (
        <button
            type="button"
            className="ot-mobile-favourite"
            data-testid={`mobile-favourite-${type}-${id}`}
            // `aria-pressed` rather than two different labels: the control is
            // the same one either way, and only its state changes.
            aria-pressed={favourite}
            aria-label={`Favourite ${type} ${name}`}
            disabled={running}
            onClick={onClick}
        >
            {
                running ? <Spin size="small"/> : (favourite ? <FaStar/> : <FaRegStar/>)
            }
        </button>
    )
}
