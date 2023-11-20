import {applyNodeChanges, Background, Controls, MarkerType, ReactFlow, ReactFlowProvider} from "reactflow";
import {useCallback, useEffect, useMemo, useState} from "react";
import ELK from 'elkjs';
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import BranchNode from "@components/links/BranchNode";
import BranchLinkNode from "@components/links/BranchLinkNode";
import {autoLayout} from "@components/links/GraphUtils";

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
                id
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
                    sourceBuild {
                        ...BuildInfo
                    }
                    targetBuild {
                        ...BuildInfo
                    }
                    autoVersioning {
                        lastEligibleBuild {
                            ...BuildInfo
                        }
                        status {
                            order {
                                targetVersion
                            }
                            running
                            mostRecentState {
                              state
                              running
                              processing
                              creation {
                                  time
                              }
                              data
                            }
                        }
                    }
                    branch {
                        ...BranchNodeInfo
                        ${gqlDownstreamDependencies(depth - 1)}
                    }
                }
            `
        }
    }

    const gqlUpstreamDependencies = (depth) => {
        if (depth <= 0) {
            return ''
        } else {
            return `
                upstreamLinks(builds: 5) {
                    qualifier
                    sourceBuild {
                        ...BuildInfo
                    }
                    targetBuild {
                        ...BuildInfo
                    }
                    branch {
                        ...BranchNodeInfo
                        ${gqlUpstreamDependencies(depth - 1)}
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
                ${gqlUpstreamDependencies(maxUpstreamDepth)}
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

    const branchToNode = (branch, id) => {
        return {
            id: id ? id : String(branch.id),
            position: {x: 0, y: 0},
            data: {branch},
            type: 'branch',
        }
    }

    const branchLinkToNode = (id, sourceBranch, targetBranch, link) => {
        return {
            id: id,
            position: {x: 0, y: 0},
            data: {
                sourceBranch,
                targetBranch,
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
                dependencyNode = branchLinkToNode(dependencyId, startNode.data.branch, childNode.data.branch, link)
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

    const collectUpstreamNodes = (startNode, nodes, edges, nodesCache, edgesCache) => {
        const {branch} = startNode.data

        branch.upstreamLinks.forEach(link => {
            const qualifier = link.qualifier
            const parentBranch = link.branch

            // Gets or creates the parent node
            const parentId = String(parentBranch.id)
            let parentNode = nodesCache[parentId]
            if (!parentNode) {
                parentNode = branchToNode(parentBranch, parentId)
                nodesCache[parentId] = parentNode
                nodes.push(parentNode)
            }

            // Node for the dependency information
            const dependencyId = qualifier ?
                `${parentNode.id}-${startNode.id}-${qualifier}` :
                `${parentNode.id}-${startNode.id}`
            let dependencyNode = nodesCache[dependencyId]
            if (!dependencyNode) {
                dependencyNode = branchLinkToNode(dependencyId, parentNode.data.branch, startNode.data.branch, link)
                nodesCache[dependencyId] = dependencyNode
                nodes.push(dependencyNode)
            }

            // Edge from the parent node to the link node
            const edgeParentId = `${parentNode.id}-${dependencyNode.id}`
            if (!edgesCache[edgeParentId]) {
                const edge = {
                    id: edgeParentId,
                    source: parentNode.id,
                    target: dependencyNode.id,
                    type: 'smoothstep',
                    markerStart: {
                        type: MarkerType.ArrowClosed,
                        width: 40,
                        height: 40,
                    },
                }
                edges.push(edge)
                edgesCache[edgeParentId] = edge
            }

            // Edge from the link node to the start node
            const edgeStartId = `${dependencyNode.id}-${startNode.id}`
            if (!edgesCache[edgeStartId]) {
                const edge = {
                    id: edgeStartId,
                    source: dependencyNode.id,
                    target: startNode.id,
                    type: 'smoothstep',
                }
                edges.push(edge)
                edgesCache[edgeStartId] = edge
            }

            // Iteration
            collectUpstreamNodes(parentNode, nodes, edges, nodesCache, edgesCache)
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
            collectUpstreamNodes(rootNode, nodes, edges, nodesCache, edgesCache)

            // Layout for the graph

            autoLayout({
                nodes,
                edges,
                nodeWidth: 220,
                nodeHeight: (node) => node.type === 'branch' ? 220 : 100,
                setNodes,
                setEdges,
            })
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