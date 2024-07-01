import {Alert, Skeleton, Space, Tree} from "antd";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {collectDownstreamNodesAsTreeData} from "@components/links/BuildLinksUtils";
import BuildLinksTreeNode from "@components/links/BuildLinksTreeNode";

export default function BuildLinksTree({build, changeDependencyLinksMode}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [treeData, setTreeData] = useState([])

    useEffect(() => {
        if (client && build) {

            const loadTreeData = async () => {
                setLoading(true)
                try {
                    const treeNodes = await collectDownstreamNodesAsTreeData(client, build)
                    setTreeData([treeNodes])
                } finally {
                    setLoading(false)
                }
            }

            // noinspection JSIgnoredPromiseFromCall
            loadTreeData()

        }
    }, [client, build])

    function switchToGraphView() {
        if (changeDependencyLinksMode) changeDependencyLinksMode('graph')
    }

    return (
        <>
            <Skeleton active loading={loading}>
                <Space direction="vertical" className="ot-line">
                    <Alert
                        message={
                            <>
                                The tree view below displays only downstream dependencies. To get also
                                upstream dependencies, switch to the <a onClick={switchToGraphView}>graph</a> view.
                            </>
                        }
                        type="info"
                        showIcon={true}
                        closable={true}
                    />
                    <Tree
                        showIcon={true}
                        defaultExpandAll={true}
                        treeData={treeData}
                        blockNode={true}
                        titleRender={node => (
                            <>
                                <BuildLinksTreeNode build={node.build} qualifier={node.qualifier}/>
                            </>
                        )}
                        showLine={true}
                    />
                </Space>
            </Skeleton>
        </>
    )
}