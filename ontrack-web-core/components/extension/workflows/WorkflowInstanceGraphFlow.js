import WorkflowInstanceGraphNode from "@components/extension/workflows/WorkflowInstanceGraphNode";
import {useCallback, useEffect, useState} from "react";
import {autoLayout} from "@components/links/GraphUtils";
import {applyNodeChanges, Background, Controls, ReactFlow} from "reactflow";
import {Skeleton} from "antd";

const nodeTypes = {
    workflowNode: WorkflowInstanceGraphNode,
}

export function WorkflowInstanceGraphFlow({instance, instanceNodeExecutions, onNodeSelected}) {

    const workflowNode = (nodeExecution, workflowNodes) => {
        return {
            id: nodeExecution.id,
            position: {x: 0, y: 0},
            data: {nodeExecution, workflowNode: workflowNodes[nodeExecution.id]},
            type: 'workflowNode',
        }
    }

    const [loading, setLoading] = useState(true)

    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    useEffect(() => {
        if (instance.id) {
            setLoading(true)

            // Indexing all workflow nodes
            const workflowNodes = {}
            instance.workflow.nodes.forEach(node => {
                workflowNodes[node.id] = node
            })

            // Each instance node ==> node
            const nodes = []
            instance.nodesExecutions.forEach(nodeExecution => {
                nodes.push(workflowNode(nodeExecution, workflowNodes))
            })

            // Edges
            const edges = []
            instance.workflow.nodes.forEach(node => {
                node.parents.forEach(({id}) => {
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

            setLoading(false)
        }
    }, [instance])

    useEffect(() => {
        if (nodes && !loading) {
            setNodes(nodes => nodes.map(node => {
                const nodeExecution = instanceNodeExecutions.find(it => it.id === node.id)
                if (nodeExecution) {
                    return {
                        ...node,
                        data: {
                            ...node.data,
                            nodeExecution,
                        }
                    }
                } else {
                    return node
                }
            }))
        }
    }, [instanceNodeExecutions])

    const onNodeClick = useCallback((event, node) => {
        if (node && onNodeSelected) {
            setNodes(nodes => nodes.map(it => ({
                ...it,
                data: {
                    ...it.data,
                    selected: it.data.workflowNode.id === node.data.workflowNode.id,
                },
            })))
            onNodeSelected(node.data)
        }
    }, [onNodeSelected])

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    );

    return (
        <>
            <div style={{height: '600px'}}>
                <Skeleton active loading={loading}>
                    <ReactFlow
                        nodes={nodes}
                        edges={edges}
                        onNodesChange={onNodesChange}
                        onNodeClick={onNodeClick}
                        fitView={true}
                        nodeTypes={nodeTypes}
                    >
                        <Background/>
                        <Controls/>
                    </ReactFlow>
                </Skeleton>
            </div>
        </>
    )
}