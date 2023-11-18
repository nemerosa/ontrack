import {Handle, Position} from "reactflow";
import {Space} from "antd";
import {useEffect, useState} from "react";
import BuildNode from "@components/links/BuildNode";
import {FaEllipsisH} from "react-icons/fa";

export default function BuildGroupNode({data}) {

    const {builds} = data

    const [group, setGroup] = useState({
        firstBuild: undefined,
        lastBuild: undefined,
        ellipsis: false,
    })

    useEffect(() => {
        // Sorting the builds from the oldest to the newest
        const sortedBuilds = builds.toSorted((a, b) => a.id - b.id)
        // First build
        const firstBuild = sortedBuilds[0]
        // Last build
        const lastBuild = sortedBuilds[sortedBuilds.length - 1]
        // Ellipsis needed?
        const ellipsis = sortedBuilds.length > 2
        // Setting the state
        setGroup({
            firstBuild,
            lastBuild,
            ellipsis,
        })
    }, [builds]);

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Handle type="source" position={Position.Right}/>
            <Handle type="source" position={Position.Top}/>
            <Handle type="source" position={Position.Bottom}/>
            <Space direction="vertical" size={16}>
                {/* First build */}
                <BuildNode data={{build: group.firstBuild}}/>
                {/* Ellipsis */}
                {
                    group.ellipsis && <FaEllipsisH/>
                }
                {/* Last build */}
                <BuildNode data={{build: group.lastBuild}}/>
            </Space>
        </>
    )
}