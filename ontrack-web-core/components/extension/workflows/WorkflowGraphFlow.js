import {applyNodeChanges, Background, Controls, ReactFlow} from "reactflow";
import {useCallback, useEffect, useState} from "react";
import WorkflowGraphNode from "@components/extension/workflows/WorkflowGraphNode";
import {autoLayout} from "@components/links/GraphUtils";

const nodeTypes = {
    workflowNode: WorkflowGraphNode,
}

export default function WorkflowGraphFlow({workflowNodes}) {

    const workflowNode = (node) => {
        return {
            id: node.id,
            position: {x: 0, y: 0},
            data: {...node},
            type: 'workflowNode',
        }
    }

    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    useEffect(() => {
        // Each workflow node ==> node
        const nodes = workflowNodes.map(node => {
            return workflowNode(node)
        })

        // Edges
        const edges = []
        workflowNodes.forEach(node => {
            node.parents?.forEach(({id}) => {
                edges.push({
                    id: `${id}-${node.id}`,
                    source: id,
                    target: node.id,
                    type: 'smoothstep',
                })
            })
        })

        // Layout for the graph

        autoLayout({
            nodes,
            edges,
            nodeWidth: 180, // Can be a function
            nodeHeight: 120, // Can be a function
            setNodes,
            setEdges,
        })

    }, [workflowNodes]);

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    );

    return (
        <>
            <div style={{height: '600px', width: '100%'}}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    fitView={true}
                    nodeTypes={nodeTypes}
                >
                    <Background/>
                    <Controls/>
                </ReactFlow>
            </div>
        </>
    )
}