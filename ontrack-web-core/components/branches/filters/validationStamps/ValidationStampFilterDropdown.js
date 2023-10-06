import {Button, Dropdown, Space, Typography} from "antd";
import {FaCaretDown, FaCheckDouble, FaEdit, FaEraser, FaEyeSlash, FaFilter, FaPlus} from "react-icons/fa";
import {useEffect, useState} from "react";
import CheckableMenuItem from "@components/common/CheckableMenuItem";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import SelectableMenuItem from "@components/common/SelectableMenuItem";
import {getLocallySelectedValidationFilter, setLocallySelectedValidationStampFilter} from "@components/storage/local";
import {isAuthorized} from "@components/common/authorizations";
import NewValidationStampFilterDialog, {
    useNewValidationStampFilterDialog
} from "@components/branches/filters/validationStamps/NewValidationStampFilterDialog";
import EditValidationStampFilterDialog, {
    useEditValidationStampFilterDialog
} from "@components/branches/filters/validationStamps/EditValidationStampFilterDialog";
import {
    gqlValidationStampFilterFragment
} from "@components/branches/filters/validationStamps/ValidationStampFilterGraphQLFragments";

/**
 * @param branch Target branch for the filters
 * @param grouping Value for the grouping preference
 * @param onGroupingChange Callback when the grouping preference changes
 * @param selectedValidationStampFilter Currently selected filter
 * @param onSelectedValidationStampFilter Sets the new currently selected filter
 * @param inlineEdition Current state of the inline edition
 * @param onInlineEdition Callback when changing the online edition mode
 */
export default function ValidationStampFilterDropdown({
                                                          branch,
                                                          grouping,
                                                          onGroupingChange,
                                                          selectedValidationStampFilter,
                                                          onSelectedValidationStampFilter,
                                                          inlineEdition,
                                                          onInlineEdition,
                                                      }) {

    const selectFilter = (filter) => {
        exitInlineEdit() // Stops any current edition
        if (onSelectedValidationStampFilter) onSelectedValidationStampFilter(filter)
        setLocallySelectedValidationStampFilter(branch.id, filter)
    }

    const onSelect = (filter) => {
        return () => {
            selectFilter(filter)
        }
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

    const onNewFilter = () => {
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
    const onEditFilter = (filter) => {
        return () => {
            editValidationStampFilterDialog.start({filter})
        }
    }

    const exitInlineEdit = () => {
        if (onInlineEdition) onInlineEdition(false)
    }

    const startInlineEdit = () => {
        if (onInlineEdition) onInlineEdition(true)
    }

    const onStartInlineEditFilter = (filter) => {
        return () => {
            // Selects the filter first
            selectFilter(filter)
            // Enters in edition mode
            startInlineEdit()
        }
    }

    const onDropFilter = () => {
        selectFilter(undefined)
    }

    const [filters, setFilters] = useState([])
    useEffect(() => {
        if (branch) {
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
                // TODO Checks also the permalink
                const initialFilter = getLocallySelectedValidationFilter(branch.id)
                if (initialFilter) {
                    selectFilter(initialFilter)
                }
            })
        }
    }, [branch]);

    const buildMenu = () => {
        const menu = []
        // Loading existing validation stamp filters
        filters.forEach(filter => {
            menu.push({
                key: filter.id,
                label: <SelectableMenuItem
                    text={filter.name}
                    value={filter.id === selectedValidationStampFilter?.id}
                    onChange={onSelect(filter)}
                    extra={
                        <>
                            {
                                isAuthorized(filter, "validation_stamp_filter", "edit") &&
                                !inlineEdition &&
                                <FaCheckDouble
                                    className="ot-command"
                                    title="Edits this filter using the branch view"
                                    onClick={onStartInlineEditFilter(filter)}
                                />
                            }
                            {
                                isAuthorized(filter, "validation_stamp_filter", "edit") &&
                                inlineEdition &&
                                <FaEyeSlash
                                    className="ot-command"
                                    title="Stops the edition of this filter"
                                    onClick={exitInlineEdit}
                                />
                            }
                        </>
                    }
                />,
            })
        })
        // Separator
        menu.push({type: 'divider'})
        // New filter
        if (isAuthorized(branch, "branch", "validation_stamp_filter_create")) {
            menu.push({
                key: 'new',
                icon: <FaPlus/>,
                label: "New filter...",
                onClick: onNewFilter,
            })
        }
        // Drop filter
        if (selectedValidationStampFilter) {
            menu.push({
                key: 'drop',
                icon: <FaEraser/>,
                label: "Clear filter",
                onClick: onDropFilter,
            })
        }
        // Separator
        menu.push({type: 'divider'})
        // TODO Displaying the VS names
        // Grouping the VS
        menu.push({
            key: 'group',
            label: <SelectableMenuItem
                text="Group validation stamps per status"
                value={grouping}
                onChange={onGroupingChange}
            />,
        })
        // OK
        setItems(menu)
    }

    const [items, setItems] = useState([])
    useEffect(() => {
        if (filters) {
            buildMenu()
        }
    }, [filters, grouping, inlineEdition, selectedValidationStampFilter]);

    return (
        <>
            <Dropdown
                menu={{items}}
                trigger={['click']}
                className="ot-action"
            >
                <Button>
                    <Space>
                        <FaFilter color={selectedValidationStampFilter ? 'orange' : undefined}/>
                        <Typography.Text>Validation filter</Typography.Text>
                        <FaCaretDown/>
                    </Space>
                </Button>
            </Dropdown>
            <NewValidationStampFilterDialog newValidationStampFilterDialog={newValidationStampFilterDialog}/>
            <EditValidationStampFilterDialog branch={branch} editValidationStampFilterDialog={editValidationStampFilterDialog}/>
        </>
    )
}