import ELK from "elkjs";

export function autoLayout({
                               nodes,
                               edges,
                               nodeWidth = 240,
                               nodeHeight = 100,
                               setNodes,
                               setEdges,
                           }) {

    const elk = new ELK()
    const elkOptions = {
        'elk.algorithm': 'layered',
        'elk.layered.spacing.nodeNodeBetweenLayers': '100',
        'elk.spacing.nodeNode': '55',
        'elk.spacing.edgeEdge': '40',
        'elk.spacing.edgeNode': '40',
        'elk.padding': '[top=20,left=20,bottom=20,right=20]',
        'elk.direction': 'RIGHT',
    }

    const getWidth = (node) => {
        return node.width || (typeof nodeWidth === 'function' ? nodeWidth(node) : nodeWidth)
    }

    const getHeight = (node) => {
        return node.height || (typeof nodeHeight === 'function' ? nodeHeight(node) : nodeHeight)
    }

    const graph = {
        id: 'root',
        layoutOptions: elkOptions,
        children: nodes.map((node) => ({
            ...node,
            // Provides the width and height for elk to use when layouting.
            // These dimensions are usually measured by React Flow and passed to the autoLayout function.
            width: getWidth(node),
            height: getHeight(node),
        })),
        edges: edges.map((edge) => ({
            ...edge,
            id: edge.id,
            sources: [edge.source],
            targets: [edge.target],
        })),
    };

    return elk
        .layout(graph)
        .then((layoutedGraph) => {
            setNodes(layoutedGraph.children.map((node) => {
                return {
                    ...node,
                    sourcePosition: 'right',
                    targetPosition: 'left',
                    // React Flow expects a position property on the node instead of `x`
                    // and `y` fields.
                    position: {
                        x: node.x,
                        y: node.y,
                    },
                    data: {
                        ...node.data,
                        visible: true,
                    }
                }
            }))
            setEdges(layoutedGraph.edges.map(edge => ({
                ...edge,
                source: edge.sources[0],
                target: edge.targets[0],
            })))
        })
        .catch(console.error);
}