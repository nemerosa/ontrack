import {
    changeEdges,
    changeNodes,
    deleteEdgeInEdges,
    deleteEdgeInNodes,
    deleteNodeInEdges,
    deleteNodeInNodes
} from "@components/extension/workflows/WorkflowGraphFlow";

describe('WorkflowGraphFlow', () => {

    const graphFixture = () => {
        return {
            nodes: [
                {
                    id: 'n1',
                    data: {
                        id: 'n1',
                        executorId: 'mock',
                        timeout: 300,
                        data: {
                            text: "N1",
                        }
                    }
                },
                {
                    id: 'n2',
                    data: {
                        id: 'n2',
                        executorId: '',
                        timeout: 300,
                        data: null,
                        parents: [
                            {id: 'n1'}
                        ]
                    }
                },
            ],
            edges: [
                {
                    id: 'n1-n2',
                    source: 'n1',
                    target: 'n2',
                }
            ]
        }
    }

    it('changes a node configuration', () => {
        const {nodes, edges} = graphFixture()
        const node = {
            oldId: 'n2',
            id: 'n2',
            executorId: 'mock',
            timeout: 300,
            data: {
                text: "N2",
            }
        }

        const newNodes = changeNodes(node, nodes)
        const newEdges = changeEdges(node, edges)

        expect(newNodes[1]).toStrictEqual({
            id: 'n2',
            data: {
                id: 'n2',
                executorId: 'mock',
                timeout: 300,
                data: {
                    text: "N2",
                },
                parents: [
                    {id: 'n1'}
                ]
            }
        })

        expect(newEdges).toBe(edges) // No change in the edges
    });

    it('renames a node', () => {
        const {nodes, edges} = graphFixture()
        const node = {
            oldId: 'n1',
            id: 'start',
            executorId: 'mock',
            timeout: 300,
            data: {
                text: "N1",
            }
        }

        const newNodes = changeNodes(node, nodes)
        const newEdges = changeEdges(node, edges)

        expect(newNodes[0]).toStrictEqual({
            id: 'start',
            data: {
                id: 'start',
                executorId: 'mock',
                timeout: 300,
                data: {
                    text: "N1",
                }
            }
        })

        expect(newNodes[1]).toStrictEqual({
            id: 'n2',
            data: {
                id: 'n2',
                executorId: '',
                timeout: 300,
                data: null,
                parents: [
                    {id: 'start'}
                ]
            }
        })

        expect(newEdges).toStrictEqual(
            [
                {
                    id: 'start-n2', // was n1-n2
                    source: 'start', // was n1
                    target: 'n2',
                }
            ]
        )
    });

    it('deletes a node', () => {

        const {nodes, edges} = graphFixture()
        const node = {
            oldId: 'n1',
            id: null,
        }

        const newNodes = deleteNodeInNodes(node, nodes)
        const newEdges = deleteNodeInEdges(node, edges)

        expect(newNodes).toStrictEqual([
            {
                id: 'n2',
                data: {
                    id: 'n2',
                    executorId: '',
                    timeout: 300,
                    data: null,
                    parents: []
                }
            },
        ])

        expect(newEdges).toStrictEqual([])

    });

    it('deletes an edge and adjust the parents of the child', () => {

        const {nodes, edges} = graphFixture()

        const newNodes = deleteEdgeInNodes('n1-n2', nodes, edges)
        const newEdges = deleteEdgeInEdges('n1-n2', edges)

        expect(newNodes).toStrictEqual([
            {
                id: 'n1',
                data: {
                    id: 'n1',
                    executorId: 'mock',
                    timeout: 300,
                    data: {
                        text: "N1",
                    },
                    parents: []
                }
            },
            {
                id: 'n2',
                data: {
                    id: 'n2',
                    executorId: '',
                    timeout: 300,
                    data: null,
                    parents: []
                }
            },
        ])

        expect(newEdges).toStrictEqual([])

    });

});