/**
 * Utilities to access the local storage
 */

const selectedBuildFilter = (branchId) => `selected_build_filter_${branchId}`

export function getLocallySelectedBuildFilter(branchId) {
    const json = localStorage.getItem(selectedBuildFilter((branchId)))
    if (json) {
        return JSON.parse(json)
    } else {
        return undefined
    }
}

export function setLocallySelectedBuildFilter(branchId, buildFilterResource) {
    if (buildFilterResource) {
        localStorage.setItem(
            selectedBuildFilter((branchId)),
            JSON.stringify(buildFilterResource)
        )
    } else {
        localStorage.removeItem(
            selectedBuildFilter((branchId))
        )
    }
}
