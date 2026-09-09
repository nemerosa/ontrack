import {Handle, Position} from "reactflow";
import {theme} from "antd";
import {getCheckpointType} from "@components/branches/views/deliverymap/checkpointTypes";

/**
 * The one React Flow node type of the delivery map.
 *
 * One node type for every checkpoint kind, dispatching on the kind through `checkpointTypes`. The
 * alternative - a React Flow node type per checkpoint kind - would put the open set of kinds into
 * React Flow's own `nodeTypes` map, which has to be a stable object identity across renders and is
 * therefore the wrong place for something an extension extends.
 *
 * The node owns the frame and the handles; the kind owns what goes inside it. That is what makes the
 * three unlike kinds read alike, which is the whole reason the concept of a checkpoint exists.
 */
export default function CheckpointNode({data}) {

    const {token} = theme.useToken()
    const checkpoint = data.checkpoint
    const {component: Component} = getCheckpointType(checkpoint.type)

    return (
        <>
            {/* Both handles on every node: an edge may run into or out of any kind of checkpoint,
                and a missing handle silently drops the edge which needed it */}
            <Handle type="target" position={Position.Left}/>
            <Handle type="source" position={Position.Right}/>
            <div
                data-testid={`checkpoint-${checkpoint.id}`}
                data-checkpoint-type={checkpoint.type}
                style={{
                    padding: token.paddingSM,
                    borderRadius: token.borderRadiusLG,
                    border: `1px solid ${token.colorBorderSecondary}`,
                    backgroundColor: token.colorBgContainer,
                    textAlign: 'left',
                }}
                title={checkpoint.description ?? undefined}
            >
                <Component checkpoint={checkpoint}/>
            </div>
        </>
    )
}
