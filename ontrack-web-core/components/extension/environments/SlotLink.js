import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {FaCog} from "react-icons/fa";
import Link from "next/link";

export default function SlotLink({slot}) {
    return (
        <>
            <Link href={slotUri(slot)} title="Slot details and configuration">
                <FaCog/>
            </Link>
        </>
    )
}