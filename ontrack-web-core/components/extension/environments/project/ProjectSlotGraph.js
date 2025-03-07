import SlotGraphNode from "@components/extension/environments/project/SlotGraphNode";
import {gql} from "graphql-request";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import {useQuery} from "@components/services/useQuery";
import {
    useProjectEnvironmentsContext
} from "@components/extension/environments/project/ProjectEnvironmentsContextProvider";
import {useCallback, useEffect, useState} from "react";
import {autoLayout} from "@components/links/GraphUtils";
import {applyNodeChanges, Background, ControlButton, Controls, MarkerType, ReactFlow} from "reactflow";
import {FaProjectDiagram} from "react-icons/fa";

const nodeTypes = {
    slotNode: SlotGraphNode,
}

export default function ProjectSlotGraph() {

    const {project, qualifier, selectedSlot, setSelectedSlot} = useProjectEnvironmentsContext()

    const {data: slotGraph, loading} = useQuery(
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
        {
            variables: {
                id: project?.id,
                qualifier,
            },
            condition: project,
            deps: [project, qualifier],
            dataFn: data => data.project.slotGraph,
        }
    )

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
        if (slotGraph) {
            const nodes = slotGraph.slotNodes.map(slotNode => ({
                id: slotNode.slot.id,
                position: {x: 0, y: 0},
                data: {...slotNode},
                type: 'slotNode',
            }))

            const edges = []
            slotGraph.slotNodes.forEach(slotNode => {
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
        }
    }, [slotGraph])

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    )

    const onPaneClick = () => {
        setSelectedSlot(null)
    }

    const relayout = () => {
        setGraph(nodes, edges)
    }

    return (
        <>
            <div style={{height: '600px', width: '100%'}}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    fitView={true}
                    nodeTypes={nodeTypes}
                    onNodesChange={onNodesChange}
                    onPaneClick={onPaneClick}
                >
                    <Background/>
                    <Controls>
                        <ControlButton title="Adjust the layout" onClick={relayout}>
                            <FaProjectDiagram/>
                        </ControlButton>
                    </Controls>
                </ReactFlow>
            </div>
        </>
    )
}
