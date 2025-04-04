import {useEffect, useState} from "react";
import {gqlBuilds} from "@components/branches/branchQueries";
import {Space} from "antd";
import BranchBuilds from "@components/branches/BranchBuilds";
import useRangeSelection from "@components/common/RangeSelection";
import {gql} from "graphql-request";
import {getLocallySelectedBuildFilter, setLocallySelectedBuildFilter,} from "@components/storage/local";
import {useRouter} from "next/router";
import ValidationStampFilterContextProvider
    from "@components/branches/filters/validationStamps/ValidationStampFilterContext";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import DisabledBranchBanner from "@components/branches/DisabledBranchBanner";
import {useEventForRefresh} from "@components/common/EventsContext";

export default function ClassicBranchView({branch}) {

    // Router (used for permalinks)
    const router = useRouter()

    // GraphQL client
    const client = useGraphQLClient()

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

    // Build created
    const buildCreated = useEventForRefresh("build.created")

    // Loading the builds
    const [loadingBuilds, setLoadingBuilds] = useState(true)
    useEffect(() => {
        if (client && branch) {
            setLoadingBuilds(true)
            client.request(gqlBuilds, {
                branchId: Number(branch.id),
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
    }, [client, branch, pagination, buildsReloads, selectedBuildFilter, buildCreated])

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
        if (client && branch) {
            setLoadingValidationStamps(true)
            client.request(
                gql`
                    query GetValidationStamps($branchId: Int!) {
                        branches(id: $branchId) {
                            validationStamps {
                                id
                                name
                                description
                                annotatedDescription
                                image
                                dataType {
                                    descriptor {
                                        id
                                        displayName
                                    }
                                    config
                                }
                            }
                        }
                    }
                `, {branchId: Number(branch.id)}
            ).then(data => {
                setValidationStamps(data.branches[0].validationStamps)
            }).finally(() => {
                setLoadingValidationStamps(false)
            })
        }
    }, [client, branch]);

    return (
        <>
            <Space direction="vertical" className="ot-line">
                <DisabledBranchBanner branch={branch}/>
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
