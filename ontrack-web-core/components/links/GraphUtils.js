import ELK from "elkjs";

export function autoLayout({
                               nodes,
                               edges,
                               nodeWidth = 220,
                               nodeHeight = 50,
                               setNodes,
                               setEdges,
                           }) {

    const elk = new ELK()
    const elkOptions = {
        'elk.algorithm': 'layered',
        'elk.layered.spacing.nodeNodeBetweenLayers': '100',
        'elk.spacing.nodeNode': '30',
        'elk.spacing.edgeEdge': '25',
        'elk.spacing.edgeNode': '25',
    }

    const getWidth = (node) => {
        return typeof nodeWidth === 'function' ? nodeWidth(node) : nodeWidth
    }

    const getHeight = (node) => {
        return typeof nodeHeight === 'function' ? nodeHeight(node) : nodeHeight
    }

    const graph = {
        id: 'root',
        layoutOptions: elkOptions,
        children: nodes.map((node) => ({
            ...node,
            targetPosition: 'right',
            sourcePosition: 'left',

            // Hardcode a width and height for elk to use when layouting.
            width: getWidth(node),
            height: getHeight(node),
        })),
        edges: edges,
    };

    elk
        .layout(graph)
        .then((layoutedGraph) => {
            setNodes(layoutedGraph.children.map((node) => {
                const width = getWidth(node)
                const height = getHeight(node)
                return {
                    ...node,
                    // React Flow expects a position property on the node instead of `x`
                    // and `y` fields.
                    position: {
                        x: node.x - width / 2,
                        y: node.y - height / 2,
                    },
                    style: {
                        width: width,
                        height: height,
                    }
                }
            }))
            setEdges(layoutedGraph.edges)
        })
        .catch(console.error);
}