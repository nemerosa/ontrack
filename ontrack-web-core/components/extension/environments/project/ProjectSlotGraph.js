import {applyNodeChanges, Background, Controls, MarkerType, ReactFlow} from "reactflow";
import {useCallback, useContext, useEffect, useState} from "react";
import SlotGraphNode from "@components/extension/environments/project/SlotGraphNode";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {autoLayout} from "@components/links/GraphUtils";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import {EventsContext} from "@components/common/EventsContext";

const nodeTypes = {
    slotNode: SlotGraphNode,
}

export default function ProjectSlotGraph({id, qualifier = ""}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    const setGraph = (nodes, edges) => {
        autoLayout({
            nodes,
            edges,
            nodeWidth: 240, // Can be a function
            nodeHeight: 60, // Can be a function
            setNodes,
            setEdges,
        })
    }

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            client.request(
                gql`
                    ${gqlSlotData}
                    ${gqlSlotPipelineData}
                    query ProjectSlotGraph(
                        $id: Int!,
                        $qualifier: String!,
                    ) {
                        project(id: $id) {
                            slotGraph(qualifier: $qualifier) {
                                slotNodes {
                                    slot {
                                        ...SlotData
                                        lastDeployedPipeline {
                                            ...SlotPipelineData
                                        }
                                    }
                                    parents {
                                        id
                                    }
                                }
                            }
                        }
                    }
                `,
                {id, qualifier}
            ).then(data => {
                const graph = data.project.slotGraph

                const nodes = graph.slotNodes.map(slotNode => ({
                    id: slotNode.slot.id,
                    position: {x: 0, y: 0},
                    data: {...slotNode},
                    type: 'slotNode',
                }))

                const edges = []
                graph.slotNodes.forEach(slotNode => {
                    slotNode.parents?.forEach(({id}) => {
                        edges.push({
                            id: `${id}-${slotNode.slot.id}`,
                            target: slotNode.slot.id,
                            source: id,
                            type: 'smoothstep',
                            markerEnd: {
                                type: MarkerType.ArrowClosed,
                                width: 20,
                                height: 20,
                            },
                        })
                    })
                })

                setGraph(nodes, edges)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id, qualifier])

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    )

    const eventsContext = useContext(EventsContext)
    eventsContext.subscribeToEvent("slot.selected", ({id}) => {
        setNodes(nodes => nodes.map(node => ({
            ...node,
            data: {
                ...node.data,
                selected: node.data.slot.id === id,
            }
        })))
    })

    return (
        <>
            <div style={{height: '600px', width: '100%', border: 'solid 1px lightgray'}}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    fitView={true}
                    nodeTypes={nodeTypes}
                    onNodesChange={onNodesChange}
                >
                    <Background/>
                    <Controls/>
                </ReactFlow>
            </div>
        </>
    )
}