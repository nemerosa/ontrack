import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gqlBuilds} from "@components/branches/branchQueries";
import {Space} from "antd";
import BranchBuilds from "@components/branches/BranchBuilds";
import useRangeSelection from "@components/common/RangeSelection";
import {gql} from "graphql-request";
import {getLocallySelectedBuildFilter, setLocallySelectedBuildFilter,} from "@components/storage/local";
import {useRouter} from "next/router";
import ValidationStampFilterContextProvider
    from "@components/branches/filters/validationStamps/ValidationStampFilterContext";

export default function ClassicBranchView({branch}) {

    // Router (used for permalinks)
    const router = useRouter()

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

    // Initially selected build filter
    let initialBuildFilter = undefined
    const {buildFilter} = router.query
    if (buildFilter) {
        try {
            initialBuildFilter = JSON.parse(buildFilter)
            // Clears the permalink
            router.replace({
                pathname: `/branch/${branch.id}`,
                query: {}
            }, undefined, {shallow: true})
        } catch (ignored) {
        }
    } else {
        initialBuildFilter = getLocallySelectedBuildFilter(branch.id)
    }

    // Selected build filter
    const [selectedBuildFilter, setSelectedBuildFilter] = useState(initialBuildFilter)
    const onSelectedBuildFilter = (resource) => {
        setLocallySelectedBuildFilter(branch.id, resource)
        setSelectedBuildFilter(resource)
    }
    const onPermalinkBuildFilter = (resource) => {
        if (resource) {
            router.replace({
                pathname: `/branch/${branch.id}`,
                query: {
                    buildFilter: JSON.stringify(resource),
                }
            }, undefined, {shallow: true})
        }
    }

    // Loading the builds
    const [loadingBuilds, setLoadingBuilds] = useState(true)
    useEffect(() => {
        if (branch) {
            setLoadingBuilds(true)
            graphQLCall(gqlBuilds, {
                branchId: branch.id,
                offset: pagination.offset,
                size: pagination.size,
                filterType: selectedBuildFilter?.type,
                // GraphQL type for the filter data is expected to be a string
                filterData: selectedBuildFilter ? JSON.stringify(selectedBuildFilter.data) : undefined,
            }).then(data => {
                const buildPage = data.branches[0].buildsPaginated
                // Builds page info (for the more button)
                setBuildsPageInfo(buildPage.pageInfo)
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
    }, [branch, pagination, buildsReloads, selectedBuildFilter])

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
                <ValidationStampFilterContextProvider branch={branch}>
                    <BranchBuilds
                        branch={branch}
                        builds={builds}
                        loadingBuilds={loadingBuilds}
                        pageInfo={buildsPageInfo}
                        onLoadMore={onLoadMoreBuilds}
                        rangeSelection={rangeSelection}
                        validationStamps={validationStamps}
                        onChange={reloadBuilds}
                        selectedBuildFilter={selectedBuildFilter}
                        onSelectedBuildFilter={onSelectedBuildFilter}
                        onPermalinkBuildFilter={onPermalinkBuildFilter}
                    />
                </ValidationStampFilterContextProvider>
            </Space>
        </>
    )
}
