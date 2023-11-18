import {applyNodeChanges, Background, Controls, MarkerType, ReactFlow, ReactFlowProvider} from "reactflow";
import {useCallback, useEffect, useMemo, useState} from "react";
import Dagre from "dagre";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import BranchNode from "@components/links/BranchNode";
import BranchLinkNode from "@components/links/BranchLinkNode";

function BranchLinksFlow({branch}) {

    const maxDownstreamDepth = 5
    const maxUpstreamDepth = 5

    const gqlBuildInfo = gql`
        fragment BuildInfo on Build {
            id
            name
            releaseProperty {
                value
            }
            creation {
                time
            }
            promotionRuns(lastPerLevel: true) {
                creation {
                    time
                }
                promotionLevel {
                    id
                    description
                    name
                    image
                    _image
                }
            }
        }
    `

    const gqlBranchInfo = gql`
        fragment BranchInfo on Branch {
            id
            name
            displayName
            project {
                id
                name
            }
        }
    `

    const gqlBranchNodeInfo = gql`
        fragment BranchNodeInfo on Branch {
            ...BranchInfo
            latestBuilds: builds(count: 1) {
                ...BuildInfo
            }
        }
    `

    const gqlDownstreamDependencies = (depth) => {
        if (depth <= 0) {
            return ''
        } else {
            return `
                downstreamLinks(builds: 5) {
                    qualifier
                    branch {
                        ...BranchNodeInfo
                        ${gqlDownstreamDependencies(depth - 1)}
                    }
                }
            `
        }
    }

    const branchQuery = `
        query RootBranch($branchId: Int!) {
            branch(id: $branchId) {
                ...BranchNodeInfo
                ${gqlDownstreamDependencies(maxDownstreamDepth)}
            }
        }
        ${gqlBuildInfo}
        ${gqlBranchInfo}
        ${gqlBranchNodeInfo}
    `

    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    const nodeTypes = useMemo(() => ({
        branch: BranchNode,
        branchLink: BranchLinkNode,
    }), []);

    const dagreGraph = new Dagre.graphlib.Graph().setDefaultEdgeLabel(() => ({}))

    const branchToNode = (branch, id) => {
        return {
            id: id ? id : String(branch.id),
            position: {x: 0, y: 0},
            data: {branch},
            type: 'branch',
        }
    }

    const branchLinkToNode = (id, sourceBranch, link) => {
        return {
            id: id,
            position: {x: 0, y: 0},
            data: {
                sourceBranch,
                link
            },
            type: 'branchLink',
        }
    }

    const collectDownstreamNodes = (startNode, nodes, edges, nodesCache, edgesCache) => {
        const {branch} = startNode.data

        branch.downstreamLinks.forEach(link => {
            const qualifier = link.qualifier
            const childBranch = link.branch

            // Gets or creates the child node
            const childId = String(childBranch.id)
            let childNode = nodesCache[childId]
            if (!childNode) {
                childNode = branchToNode(childBranch, childId)
                nodesCache[childId] = childNode
                nodes.push(childNode)
            }

            // Node for the dependency information
            const dependencyId = qualifier ?
                `${startNode.id}-${childNode.id}-${qualifier}` :
                `${startNode.id}-${childNode.id}`
            let dependencyNode = nodesCache[dependencyId]
            if (!dependencyNode) {
                dependencyNode = branchLinkToNode(dependencyId, startNode.data.branch, link)
                nodesCache[dependencyId] = dependencyNode
                nodes.push(dependencyNode)
            }

            // Edge from the start node to the link node
            const edgeStartId = `${startNode.id}-${dependencyNode.id}`
            if (!edgesCache[edgeStartId]) {
                const edge = {
                    id: edgeStartId,
                    source: startNode.id,
                    target: dependencyNode.id,
                    type: 'smoothstep',
                    markerStart: {
                        type: MarkerType.ArrowClosed,
                        width: 40,
                        height: 40,
                    },
                }
                edges.push(edge)
                edgesCache[edgeStartId] = edge
            }

            // Edge from the link node to the child node
            const edgeChildId = `${dependencyNode.id}-${childNode.id}`
            if (!edgesCache[edgeChildId]) {
                const edge = {
                    id: edgeChildId,
                    source: dependencyNode.id,
                    target: childNode.id,
                    type: 'smoothstep',
                }
                edges.push(edge)
                edgesCache[edgeChildId] = edge
            }

            // Iteration
            collectDownstreamNodes(childNode, nodes, edges, nodesCache, edgesCache)
        })
    }

    useEffect(() => {
        graphQLCall(
            branchQuery,
            {branchId: branch.id}
        ).then(data => {

            // Root branch
            const rootBranch = data.branch

            // Nodes & edges to build
            const nodes = []
            const edges = []

            // Cache for the nodes & edges
            const nodesCache = {}
            const edgesCache = {}

            // Root node
            const rootNode = branchToNode(rootBranch)
            rootNode.data.selected = true
            nodes.push(rootNode)
            nodesCache[rootNode.id] = rootNode

            collectDownstreamNodes(rootNode, nodes, edges, nodesCache, edgesCache)

            // TODO Collect upstream dependencies

            // Layout for the graph

            dagreGraph.setGraph({rankdir: 'LR'})

            const nodeWidth = 180
            const nodeHeight = 30

            edges.forEach((edge) => dagreGraph.setEdge(edge.source, edge.target))
            nodes.forEach((node) => dagreGraph.setNode(node.id, {
                width: nodeWidth,
                height: nodeHeight,
            }))

            Dagre.layout(dagreGraph)

            nodes.forEach(node => {
                const nodeWithPosition = dagreGraph.node(node.id)
                node.targetPosition = 'left'
                node.sourcePosition = 'right'

                node.position = {
                    x: nodeWithPosition.x - nodeWidth / 2,
                    y: nodeWithPosition.y - nodeHeight / 2,
                }
            })

            // OK
            setNodes(nodes)
            setEdges(edges)
        })
    }, [branch]);

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    );

    return (
        <>
            <div style={{height: '800px'}}>
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

export default function BranchLinksGraph({branch}) {
    return (
        <>
            <ReactFlowProvider>
                <BranchLinksFlow branch={branch}/>
            </ReactFlowProvider>
        </>
    )
}