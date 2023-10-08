import {createContext, useCallback, useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {
    gqlValidationStampFilterFragment
} from "@components/branches/filters/validationStamps/ValidationStampFilterGraphQLFragments";
import {getLocallySelectedValidationFilter, setLocallySelectedValidationStampFilter} from "@components/storage/local";
import NewValidationStampFilterDialog
    , {
    useNewValidationStampFilterDialog
} from "@components/branches/filters/validationStamps/NewValidationStampFilterDialog";
import EditValidationStampFilterDialog
    , {
    useEditValidationStampFilterDialog
} from "@components/branches/filters/validationStamps/EditValidationStampFilterDialog";

// noinspection JSUnusedLocalSymbols
export const ValidationStampFilterContext = createContext({
    // List of available filters
    filters: [],
    // Selected filter
    selectedFilter: undefined,
    // Selects a filter
    selectFilter: (filter) => {
    },
    // Toggles a validation stamp for the current filter
    toggleValidationStamp: (validationStamp) => {
    },
    // Inline edition management
    inlineEdition: false,
    startInlineEdition: (filter) => {
    },
    stopInlineEdition: () => {
    },
    // Dialogs
    newFilter: () => {
    },
    editFilter: (filter) => {
    },
})

export default function ValidationStampFilterContextProvider({branch, children}) {

    const [filters, setFilters] = useState([])
    useEffect(() => {
        graphQLCall(
            gql`
                query GetValidationStampFilters(
                    $branchId: Int!,
                ) {
                    branch(id: $branchId) {
                        validationStampFilters(all: true) {
                            ...validationStampFilterContent
                        }
                    }
                }

                ${gqlValidationStampFilterFragment}
            `, {
                branchId: branch.id,
            }
        ).then(data => {
            setFilters(data.branch.validationStampFilters)
            // Selection of the initial filter
            // TODO Checks also the permalink
            const initialFilter = getLocallySelectedValidationFilter(branch.id)
            if (initialFilter) {
                setSelectedFilter(initialFilter)
            }
        })
    }, [branch])

    const [selectedFilter, setSelectedFilter] = useState()

    const selectFilter = (filter) => {
        // TODO exitInlineEdit() // Stops any current edition
        setLocallySelectedValidationStampFilter(branch.id, filter)
        setSelectedFilter(filter)
    }

    const toggleValidationStamp = useCallback((validationStamp) => {
        if (selectedFilter) {
            let vsNames = selectedFilter.vsNames
            if (vsNames.indexOf(validationStamp.name) >= 0) {
                vsNames = vsNames.filter(it => it !== validationStamp.name).sort()
            } else {
                vsNames = [...vsNames, validationStamp.name].sort()
            }
            graphQLCall(
                gql`
                    mutation UpdateValidationStampFilter(
                        $id: Int!,
                        $vsNames: [String!]!,
                    ) {
                        updateValidationStampFilter(input: {
                            id: $id,
                            vsNames: $vsNames,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `, {
                    id: selectedFilter.id,
                    vsNames,
                }
            ).then(data => {
                setSelectedFilter({
                    ...selectedFilter,
                    vsNames,
                })
            })
        }
    }, [selectedFilter])

    const [inlineEdition, setInlineEdition] = useState(false)

    const startInlineEdition = (filter) => {
        selectFilter(filter)
        setInlineEdition(true)
    }

    const stopInlineEdition = () => {
        setInlineEdition(false)
    }

    const newValidationStampFilterDialog = useNewValidationStampFilterDialog({
        onSuccess: (values) => {
            // Creates the filter
            graphQLCall(
                gql`
                    mutation CreateValidationStampFilter(
                        $name: String!
                    ) {
                        createValidationStampFilter(input: {
                            name: $name
                        }) {
                            errors {
                                message
                            }
                            validationStampFilter {
                                ...validationStampFilterContent
                            }
                        }
                    }

                    ${gqlValidationStampFilterFragment}
                `, values
            ).then(data => {
                setFilters([
                    ...filters,
                    data.createValidationStampFilter.validationStampFilter,
                ].sort())
                // TODO Enters in edition mode immediately
            })
        }
    })

    const newFilter = () => {
        newValidationStampFilterDialog.start()
    }

    const editValidationStampFilterDialog = useEditValidationStampFilterDialog({
        onSuccess: (values, {filter}) => {
            graphQLCall(
                gql`
                    mutation UpdateValidationStampFilter(
                        $id: Int!,
                        $vsNames: [String!]!,
                    ) {
                        updateValidationStampFilter(input: {
                            id: $id,
                            vsNames: $vsNames,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `,
                {
                    id: filter.id,
                    vsNames: values.vsNames,
                }
            ).then(() => {
                const updatedFilter = {
                    ...filter,
                    vsNames: values.vsNames,
                }
                setFilters(filters.map(it => {
                    if (it.id === filter.id) {
                        return updatedFilter
                    } else {
                        return it
                    }
                }))
                selectFilter(updatedFilter)
            })
        }
    })

    const editFilter = (filter) => {
        editValidationStampFilterDialog.start({filter})
    }

    const context = {
        filters,
        selectedFilter,
        selectFilter,
        toggleValidationStamp,
        inlineEdition,
        startInlineEdition,
        stopInlineEdition,
        newFilter,
        editFilter,
    }

    return (
        <ValidationStampFilterContext.Provider value={context}>
            <>
                {children}
                <NewValidationStampFilterDialog newValidationStampFilterDialog={newValidationStampFilterDialog}/>
                <EditValidationStampFilterDialog branch={branch}
                                                 editValidationStampFilterDialog={editValidationStampFilterDialog}/>
            </>
        </ValidationStampFilterContext.Provider>
    )
}