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
            {/* The name is the link itself rather than a `Typography.Text` inside one: antd's
                Text sets its own colour, which would leave a slot looking unlike every other
                checkpoint on the map, all of which name a linked entity */}
            <Link href={slotUri({id: slotId})} title="Slot details and configuration">
                {checkpoint.name}
            </Link>
            {
                unreachable ?
                    // Short enough to fit the width `checkpointTypes` reserves for the node.
                    // Nothing here may overflow it: elk lays the map out against that width, and
                    // a node drawn wider than it was laid out covers whatever sits beside it.
                    <Typography.Text
                        type="secondary"
                        italic
                        title="An admission rule of this slot excludes this branch, so no build of it can ever be deployed here, however far it is promoted."
                    >
                        <Space size={4}>
                            <FaBan/>
                            Unreachable from this branch
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
