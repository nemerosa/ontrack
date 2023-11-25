import {applyNodeChanges, Background, Controls, ReactFlow, ReactFlowProvider} from "reactflow";
import {gql} from "graphql-request";
import {useCallback, useEffect, useMemo, useState} from "react";
import BuildNode from "@components/links/BuildNode";
import BuildGroupNode from "@components/links/BuildGroupNode";
import {autoLayout} from "@components/links/GraphUtils";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

function BuildLinksFlow({build}) {

    const client = useGraphQLClient()

    const maxDownstreamDepth = 5
    const maxUpstreamDepth = 5

    const gqlBuildMinInfo = gql`
        fragment BuildMinInfo on Build {
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

    const gqlBuildInfo = gql`
        ${gqlBuildMinInfo}
        fragment BuildInfo on Build {
            ...BuildMinInfo
            branch {
                id
                name
                displayName
                project {
                    id
                    name
                }
            }
        }
    `

    const gqlBuildNodeInfo = gql`
        ${gqlBuildInfo}
        fragment BuildNodeInfo on Build {
            ...BuildInfo
            lastBuildInfo: branch {
                lastBuild: builds(count: 1) {
                    ...BuildInfo
                }
            }
            previousBuild {
                ...BuildInfo
            }
            nextBuild {
                ...BuildInfo
            }
            autoVersioning(buildId: $buildId, direction: DOWN) {
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
                    }
                }
            }
        }
    `

    const gqlDownstreamBuildDependencies = (depth) => {
        if (depth <= 0) {
            return ''
        } else {
            return `
              usingQualified {
                pageItems {
                    qualifier
                    build {
                        ...BuildNodeInfo
                        ${gqlDownstreamBuildDependencies(depth - 1)}
                    }
                }
              }
            `;
        }
    };

    const gqlUpstreamBuildDependencies = (depth) => {
        if (depth <= 0) {
            return ''
        } else {
            return `
              usedByQualified {
                pageItems {
                    qualifier
                    build {
                        ...BuildNodeInfo
                        ${gqlUpstreamBuildDependencies(depth - 1)}
                    }
                }
              }
            `;
        }
    };

    // noinspection GraphQLUnresolvedReference
    const buildQuery = `
        query RootBuild($buildId: Int!) {
            build(id: $buildId) {
                ...BuildNodeInfo
                ${gqlDownstreamBuildDependencies(maxDownstreamDepth)}
                ${gqlUpstreamBuildDependencies(maxUpstreamDepth)}
            }
        }
        ${gqlBuildNodeInfo}
    `

    const [nodes, setNodes] = useState([])
    const [edges, setEdges] = useState([])

    const nodeTypes = useMemo(() => ({
        build: BuildNode,
        group: BuildGroupNode,
    }), []);

    const buildToNode = (build, id) => {
        return {
            id: id ? id : String(build.id),
            position: {x: 0, y: 0},
            data: {build},
            type: 'build',
        }
    }

    /**
     * A _box_ is the aggregation of build upstream dependencies for the same project and qualifier.
     */
    const collectUpstreamBoxes = (build, currentBox, boxes) => {
        build.usedByQualified.pageItems.forEach(link => {
            const parent = link.build
            const parentProject = parent.branch.project.name
            const parentBoxId = parentProject

            let parentBox = boxes[parentBoxId]
            if (!parentBox) {
                parentBox = {
                    id: parentBoxId,
                }
                boxes[parentBoxId] = parentBox
            }

            if (parentBox.builds) {
                parentBox.builds.push(parent)
            } else {
                parentBox.builds = [parent]
            }

            if (currentBox) {
                if (!parentBox.children) {
                    parentBox.children = {}
                }
                parentBox.children[currentBox.id] = currentBox
            }

            collectUpstreamBoxes(parent, parentBox, boxes)
        })
    }

    const collectUpstreamNodes = (rootNodeId, build, nodes, edges, nodesCache, edgesCache) => {

        const boxes = {}
        collectUpstreamBoxes(build, {
            id: rootNodeId,
        }, boxes)
        console.log({boxes})

        for (const boxId in boxes) {
            const box = boxes[boxId]
            console.log({box})
            // We take boxes into account only if they contain builds
            if (box.builds.length > 0) {
                let boxNode
                // If only one build, it's a regular build node
                if (box.builds.length === 1) {
                    const boxBuild = box.builds[0]
                    boxNode = buildToNode(boxBuild, boxId)
                    nodes.push(boxNode)
                }
                // If more than 1 build, it's a group
                else {
                    boxNode = {
                        id: boxId,
                        position: {x: 0, y: 0},
                        data: {
                            builds: box.builds,
                        },
                        type: 'group',
                    }
                    nodes.push(boxNode)
                }
                // Linking to the children
                for (const childId in box.children) {
                    let edgeId = `${boxId}-${childId}`
                    if (!edgesCache[edgeId]) {
                        const edge = {
                            id: edgeId,
                            source: String(boxNode.id),
                            target: String(childId),
                            type: 'smoothstep',
                        }
                        edges.push(edge)
                        edgesCache[edgeId] = edgeId
                    }
                }
            }
        }
    }

    const collectDownstreamNodes = (build, nodes, edges, nodesCache, edgesCache) => {
        // Adds the current build as a node
        const nodeId = String(build.id)
        if (!nodesCache[nodeId]) {
            const node = buildToNode(build);
            nodes.push(node)
            nodesCache[nodeId] = node
        }
        // Direct dependencies
        build.usingQualified.pageItems.forEach(link => {
            const child = link.build
            // Build the child node
            collectDownstreamNodes(child, nodes, edges, nodesCache, edgesCache)
            // Adding an edge
            let edgeId = `${build.id}-${child.id}`
            if (link.qualifier) {
                edgeId += `-${link.qualifier}`
            }
            if (!edgesCache[edgeId]) {
                const edge = {
                    id: edgeId,
                    source: String(build.id),
                    target: String(child.id),
                    type: 'smoothstep',
                    label: link.qualifier,
                }
                edges.push(edge)
                edgesCache[edgeId] = edgeId
            }
        })
        // OK
        return nodeId
    }

    useEffect(() => {
        if (client) {
            client.request(
                buildQuery,
                {buildId: build.id}
            ).then(data => {
                // Nodes & edges to build
                const nodes = []
                const edges = []

                // Cache for the nodes & edges
                const nodesCache = {}
                const edgesCache = {}

                // Building all downstream nodes recursively
                const rootNodeId = collectDownstreamNodes(
                    data.build,
                    nodes,
                    edges,
                    nodesCache,
                    edgesCache,
                )

                // Selecting the root node
                nodesCache[rootNodeId].data.selected = true

                // Building all upstream nodes recursively
                collectUpstreamNodes(
                    rootNodeId,
                    data.build,
                    nodes,
                    edges,
                    nodesCache,
                    edgesCache,
                )

                // Layout for the graph

                autoLayout({
                    nodes,
                    edges,
                    nodeWidth: (node) => node.type === 'group' ? 200 : 180,
                    nodeHeight: (node) => node.type === 'group' ? 300 : 120,
                    setNodes,
                    setEdges,
                })
            })
        }
    }, [client, build])

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    );

    return (
        <>
            <div style={{height: '400px'}}>
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

export default function BuildLinksGraph({build}) {
    return (
        <>
            <ReactFlowProvider>
                <BuildLinksFlow build={build}/>
            </ReactFlowProvider>
        </>
    )
}