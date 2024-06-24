import {Skeleton, Tree} from "antd";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {collectDownstreamNodesAsTreeData} from "@components/links/BuildLinksUtils";

export default function BuildLinksTree({build}) {

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

    return (
        <>
            <Skeleton active loading={loading}>
                <Tree
                    showIcon={true}
                    defaultExpandAll={true}
                    treeData={treeData}
                />
            </Skeleton>
        </>
    )
}