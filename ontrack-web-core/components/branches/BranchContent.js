import {Space} from "antd";
import BranchSetupPanel from "@components/branches/BranchSetupPanel";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gqlBuilds} from "@components/branches/branchQueries";
import BranchBuilds from "@components/branches/BranchBuilds";

export default function BranchContent({branch}) {

    // Pagination status
    const [pagination, setPagination] = useState({
        offset: 0,
        size: 10,
    })

    // Current build filter
    const [buildFilter, setBuildFilter] = useState({
        type: undefined,
        data: undefined
    })

    // List of builds
    const [builds, setBuilds] = useState([])
    const [buildsPageInfo, setBuildsPageInfo] = useState({
        nextPage: {}
    })

    // Loading the builds
    const [loadingBuilds, setLoadingBuilds] = useState(true)
    useEffect(() => {
        setLoadingBuilds(true)
        graphQLCall(gqlBuilds, {
            branchId: branch.id,
            offset: pagination.offset,
            size: pagination.size,
            filterType: buildFilter.type,
            // GraphQL type for the filter data is expected to be a string
            filterData: JSON.stringify(buildFilter.data),
        }).then(data => {
            const buildPage = data.branches[0].buildsPaginated
            // Builds page info (for the more button)
            setBuildsPageInfo(buildPage.pageInfo)
            // TODO Computing validation status groups
            // Completing the builds list of a pagination request (based on offset > 0)
            if (pagination.offset > 0) {
                setBuilds(builds.concat(buildPage.pageItems))
            } else {
                setBuilds(buildPage.pageItems)
            }
        }).finally(() => {
            setLoadingBuilds(false)
        })
    }, [branch, pagination, buildFilter])

    // Loading more builds
    const onLoadMoreBuilds = () => {
        if (buildsPageInfo.nextPage) {
            setPagination(buildsPageInfo.nextPage)
        }
    }

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <BranchSetupPanel/>
                <BranchBuilds builds={builds} pageInfo={buildsPageInfo} onLoadMore={onLoadMoreBuilds}/>
            </Space>
        </>
    )
}