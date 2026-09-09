import {useCallback, useEffect, useState} from "react";
import {applyNodeChanges, Background, Controls, ReactFlow} from "reactflow";
import {autoLayout} from "@components/links/GraphUtils";
import CheckpointNode from "@components/branches/views/deliverymap/CheckpointNode";
import {getCheckpointType} from "@components/branches/views/deliverymap/checkpointTypes";
import {toFlowEdges, toFlowNodes} from "@components/branches/views/deliverymap/deliveryMapModel";

// Defined once, outside the component: React Flow warns and re-creates every node when this object
// changes identity between renders
const nodeTypes = {
    checkpoint: CheckpointNode,
}

/**
 * The delivery map, drawn.
 *
 * React Flow with elk's `layered` algorithm through the shared `autoLayout` helper, which is what
 * every other graph in the product uses. `layered` is the right family for a directed graph read
 * left to right, and it breaks cycles safely if a misconfiguration produces one.
 *
 * A node's size comes from its checkpoint kind rather than from measuring it, because elk needs the
 * sizes before anything is rendered.
 *
 * @param map The delivery map to draw
 * @param height Height of the drawing area
 */
export default function DeliveryMapGraph({map, height = 600}) {

    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    useEffect(() => {
        if (map) {
            autoLayout({
                nodes: toFlowNodes(map.checkpoints),
                edges: toFlowEdges(map.edges),
                nodeWidth: node => getCheckpointType(node.data?.checkpoint?.type).width,
                nodeHeight: node => getCheckpointType(node.data?.checkpoint?.type).height,
                setNodes,
                setEdges,
            })
        }
    }, [map])

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    )

    return (
        <div style={{height, width: '100%'}} data-testid="delivery-map-graph">
            <ReactFlow
                nodes={nodes}
                edges={edges}
                nodeTypes={nodeTypes}
                onNodesChange={onNodesChange}
                fitView={true}
                nodesConnectable={false}
                // The map is configuration read, never configuration edited: dragging a node about
                // is fine, deleting one would suggest the picture is the thing rather than a reading
                // of it
                deleteKeyCode={null}
            >
                <Background/>
                <Controls showInteractive={false}/>
            </ReactFlow>
        </div>
    )
}
