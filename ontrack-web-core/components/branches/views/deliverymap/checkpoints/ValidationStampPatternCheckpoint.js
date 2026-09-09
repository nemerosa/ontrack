import {useState} from "react";
import {Button, Space, theme, Typography} from "antd";
import {FaChevronDown, FaChevronRight} from "react-icons/fa";
import ValidationStampCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampCheckpoint";
import {summariseMembers} from "@components/branches/views/deliverymap/checkpoints/aggregateSummary";

/**
 * The aggregate checkpoint: one node standing for every validation stamp matched by an auto
 * promotion pattern, rather than one node per stamp.
 *
 * It is labelled with the PATTERN, because that is what the configuration actually says: everything
 * matching this, not these forty named things.
 *
 * Expanding shows the members INSIDE this node rather than adding nodes to the graph. Adding nodes
 * would re-run the layout and reshuffle the whole map under the user's cursor, which is the very
 * thing #1707 is about to spend an issue avoiding on refresh.
 *
 * @param checkpoint The aggregate checkpoint to draw
 */
export default function ValidationStampPatternCheckpoint({checkpoint}) {

    const {token} = theme.useToken()
    const [expanded, setExpanded] = useState(false)

    const members = checkpoint.members ?? []
    const {total, passed} = summariseMembers(members)

    return (
        <Space direction="vertical" size={token.marginXXS} style={{width: '100%'}}>
            <Space size={token.marginXXS}>
                <Typography.Text code>{checkpoint.name}</Typography.Text>
            </Space>
            <Button
                type="text"
                size="small"
                icon={expanded ? <FaChevronDown/> : <FaChevronRight/>}
                onClick={() => setExpanded(!expanded)}
                data-testid={`checkpoint-expand-${checkpoint.id}`}
            >
                {`${passed} of ${total} passed`}
            </Button>
            {
                expanded &&
                <Space direction="vertical" size={token.marginXXS} style={{width: '100%'}}>
                    {
                        members.map(member =>
                            <ValidationStampCheckpoint key={member.id} checkpoint={member}/>
                        )
                    }
                </Space>
            }
        </Space>
    )
}
