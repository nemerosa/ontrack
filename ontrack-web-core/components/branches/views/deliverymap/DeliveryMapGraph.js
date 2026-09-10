import {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {theme} from "antd";
import {FaEye, FaEyeSlash, FaProjectDiagram} from "react-icons/fa";
import {applyNodeChanges, Background, ControlButton, Controls, ReactFlow} from "reactflow";
import {autoLayout} from "@components/links/GraphUtils";
import CheckpointNode from "@components/branches/views/deliverymap/CheckpointNode";
import {getCheckpointType} from "@components/branches/views/deliverymap/checkpointTypes";
import {
    toFlowEdges,
    toFlowNodes,
    topologyKey,
    withCheckpointContents,
    withEdgeColor,
} from "@components/branches/views/deliverymap/deliveryMapModel";

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
 * The map REFRESHES ITSELF, and the layout deliberately does not follow it. Almost nothing here
 * changes minute to minute - the configuration is static, and only the build on each checkpoint
 * moves - so the layout runs when the TOPOLOGY changes and the node contents are updated in place
 * the rest of the time. Laying out again on every fetch would reshuffle the whole map under the
 * user's cursor once a minute, and throw away every node they had dragged.
 *
 * Its own controls sit in React Flow's control bar, where every other graph of the product puts
 * theirs. They act on the drawing and on nothing else: the validation stamp filter, which is a
 * statement about the branch, lives above the view switch so that it follows the user from one
 * content view to the next.
 *
 * @param map The delivery map to draw
 * @param showValidationStamps Whether the validation stamps are drawn, for the control's own state
 * @param onToggleValidationStamps Called when the user asks for the stamps to be shown or hidden
 * @param height Height of the drawing area
 */
export default function DeliveryMapGraph({
                                             map,
                                             showValidationStamps = true,
                                             onToggleValidationStamps,
                                             height = 600,
                                         }) {

    // The edges are painted in a theme colour rather than React Flow's own, whose default all but
    // hides the arrowheads (#1717). Read here and applied below because the mapping is a pure
    // function: a hook inside it would make it untestable on its own.
    const {token} = theme.useToken()
    const edgeColor = token.colorTextTertiary

    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    // Bumped by the manual relayout control. A counter rather than a boolean: asking for the layout
    // again after having dragged one node about has to work a second time.
    const [relayoutCount, setRelayoutCount] = useState(0)

    // What the layout actually depends on. A refresh brings back a new map object every time, so
    // keying the layout on the map itself would run elk on every tick; keying it on the shape of
    // that map runs it only when the shape is genuinely different.
    const topology = useMemo(() => topologyKey(map), [map])

    // The map as it is RIGHT NOW, for the layout to read when elk answers rather than when it was
    // asked. elk is asynchronous, so a refresh landing while it works would otherwise have its
    // builds overwritten by the ones captured when the layout started, and the map would stay a
    // tick behind until the next one.
    const mapRef = useRef(map)
    mapRef.current = map

    // eslint-disable-next-line react-hooks/exhaustive-deps -- `map` is read here but deliberately
    // not depended upon: `topology` is the part of it this effect answers to. A change of map with
    // no change of topology is handled by the effect below, in place.
    useEffect(() => {
        if (!map) return
        // elk answers asynchronously, so two map changes in quick succession - toggling stamps
        // during inline edition of the validation stamp filter fires one per toggle - put two
        // layouts in flight at once. Whichever resolves last would otherwise win, and a stale one
        // paints checkpoints the current map no longer holds.
        let current = true
        autoLayout({
            nodes: toFlowNodes(map.checkpoints),
            edges: toFlowEdges(map.edges),
            nodeWidth: node => getCheckpointType(node.data?.checkpoint?.type).width,
            nodeHeight: node => getCheckpointType(node.data?.checkpoint?.type).height,
            setNodes: nodes => {
                if (current) setNodes(withCheckpointContents(nodes, mapRef.current?.checkpoints))
            },
            setEdges: edges => {
                if (current) setEdges(edges)
            },
        })
        return () => {
            current = false
        }
    }, [topology, relayoutCount])

    // Every fetch, including the ones which changed nothing: the builds move, the shape does not.
    // Positions are left exactly as the layout - or the user's own dragging - left them.
    useEffect(() => {
        if (!map) return
        setNodes(nodes => withCheckpointContents(nodes, map.checkpoints))
    }, [map])

    // Painted at render rather than inside the layout effect: a change of theme then repaints the
    // map instead of relaying it out, which would throw away every node the user had dragged.
    const paintedEdges = useMemo(() => withEdgeColor(edges, edgeColor), [edges, edgeColor])

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    )

    return (
        <div style={{height, width: '100%'}} data-testid="delivery-map-graph">
            <ReactFlow
                nodes={nodes}
                edges={paintedEdges}
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
                <Controls showInteractive={false}>
                    {/* The layout is not recomputed while the map is unchanged, so a map the user
                        has pulled apart - or one whose nodes elk placed awkwardly - is put back in
                        order from here. The same control the other graphs of the product carry. */}
                    <ControlButton
                        title="Adjust the layout"
                        onClick={() => setRelayoutCount(count => count + 1)}
                        data-testid="delivery-map-relayout"
                    >
                        <FaProjectDiagram/>
                    </ControlButton>
                    <ControlButton
                        title={
                            showValidationStamps ?
                                "Hide the validation stamps" :
                                "Show the validation stamps"
                        }
                        onClick={onToggleValidationStamps}
                        data-testid="delivery-map-toggle-stamps"
                    >
                        {showValidationStamps ? <FaEye/> : <FaEyeSlash/>}
                    </ControlButton>
                </Controls>
            </ReactFlow>
        </div>
    )
}
