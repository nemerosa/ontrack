import {Space, Tag, Typography} from "antd";
import Link from "next/link";
import {FaBan} from "react-icons/fa";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import CheckpointArrival from "@components/branches/views/deliverymap/checkpoints/CheckpointArrival";

/**
 * A slot on the delivery map.
 *
 * The kind the core has never heard of: the environments extension contributes it, and this is the
 * component the checkpoint registry dispatches to for it.
 *
 * It is the one kind whose build may belong to **another branch** - a slot names what is deployed in
 * it, and what is deployed in production is a fact about the project rather than about the branch
 * being read. That exception is deliberate and is recorded in ADR 0009; it is drawn distinctly, in
 * words, because a build name alone gives no hint that it comes from somewhere else.
 *
 * A slot this branch can never reach is drawn too, and says so instead of naming a build. Leaving it
 * out would be the map answering "how do I get to production?" with silence.
 *
 * @param checkpoint The checkpoint to draw
 */
export default function SlotCheckpoint({checkpoint}) {

    const {slotId, unreachable, otherBranch} = checkpoint.data ?? {}

    return (
        <Space direction="vertical" size={0}>
            {/* To the slot rather than to the environment: the admission rules the map only draws
                the shape of are readable on the slot page */}
            <Link href={slotUri({id: slotId})} title="Slot details and configuration">
                <Typography.Text strong>{checkpoint.name}</Typography.Text>
            </Link>
            {
                unreachable ?
                    <Typography.Text type="secondary" italic>
                        <Space size={4}>
                            <FaBan/>
                            No build of this branch can be deployed here
                        </Space>
                    </Typography.Text> :
                    <>
                        <CheckpointArrival arrival={checkpoint.arrival} nothingText="Never deployed"/>
                        {
                            otherBranch &&
                            <Tag color="orange" bordered={false}>{`from ${otherBranch}`}</Tag>
                        }
                    </>
            }
        </Space>
    )
}
