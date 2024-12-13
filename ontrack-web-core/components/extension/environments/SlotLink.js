import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {FaCog} from "react-icons/fa";
import Link from "next/link";
import {Space} from "antd";

export default function SlotLink({slot, text = ""}) {
    return (
        <>
            <Link href={slotUri(slot)} title="Slot details and configuration">
                <Space>
                    <FaCog/>
                    {text}
                </Space>
            </Link>
        </>
    )
}