import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gqlBuilds} from "@components/branches/branchQueries";
import {Space} from "antd";
import BranchBuilds from "@components/branches/BranchBuilds";
import useRangeSelection from "@components/common/RangeSelection";
import {gql} from "graphql-request";

export default function ClassicBranchView({branch}) {

    // Pagination status
    const [pagination, setPagination] = useState({
        offset: 0,
        size: 10,
    })

    // List of builds
    const [builds, setBuilds] = useState([])
    const [buildsPageInfo, setBuildsPageInfo] = useState({
        nextPage: {}
    })
    const [buildsReloads, setBuildsReloads] = useState(0)

    // Loading the builds
    const [loadingBuilds, setLoadingBuilds] = useState(true)
    useEffect(() => {
        if (branch) {
            setLoadingBuilds(true)
            graphQLCall(gqlBuilds, {
                branchId: branch.id,
                offset: pagination.offset,
                size: pagination.size,
                // filterType: buildFilter.type,
                // GraphQL type for the filter data is expected to be a string
                // filterData: JSON.stringify(buildFilter.data),
            }).then(data => {
                const buildPage = data.branches[0].buildsPaginated
                // Builds page info (for the more button)
                setBuildsPageInfo(buildPage.pageInfo)
                // TODO Computing validation status groups
                // Completing the builds list of a pagination request (based on offset > 0)
                if (pagination.offset > 0) {
                    setBuilds([...builds, ...buildPage.pageItems])
                } else {
                    setBuilds(buildPage.pageItems)
                }
            }).finally(() => {
                setLoadingBuilds(false)
            })
        }
    }, [branch, pagination, buildsReloads])

    // Loading more builds
    const onLoadMoreBuilds = () => {
        if (buildsPageInfo.nextPage) {
            setPagination(buildsPageInfo.nextPage)
        }
    }

    // Forcing the reload of the builds
    const reloadBuilds = () => {
        setBuildsReloads(buildsReloads + 1)
    }

    // Range selection
    const rangeSelection = useRangeSelection()

    // Loading validation stamps
    const [validationStamps, setValidationStamps] = useState([])
    const [loadingValidationStamps, setLoadingValidationStamps] = useState(true)
    useEffect(() => {
        if (branch) {
            setLoadingValidationStamps(true)
            graphQLCall(
                gql`
                    query GetValidationStamps($branchId: Int!) {
                        branches(id: $branchId) {
                            validationStamps {
                                id
                                name
                                description
                                annotatedDescription
                                image
                            }
                        }
                    }
                `, {branchId: branch.id}
            ).then(data => {
                setValidationStamps(data.branches[0].validationStamps)
            }).finally(() => {
                setLoadingValidationStamps(false)
            })
        }
    }, [branch]);

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <BranchBuilds
                    builds={builds}
                    loadingBuilds={loadingBuilds}
                    pageInfo={buildsPageInfo}
                    onLoadMore={onLoadMoreBuilds}
                    rangeSelection={rangeSelection}
                    validationStamps={validationStamps}
                    loadingValidationStamps={loadingValidationStamps}
                    onChange={reloadBuilds}
                />
            </Space>
        </>
    )
}
