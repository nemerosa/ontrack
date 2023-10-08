import {Button, Dropdown, Space, Typography} from "antd";
import {FaCaretDown, FaCheckDouble, FaEdit, FaEraser, FaEyeSlash, FaFilter, FaPlus} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
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
import {ValidationStampFilterContext} from "@components/branches/filters/validationStamps/ValidationStampFilterContext";

/**
 * @param branch Target branch for the filters
 * @param grouping Value for the grouping preference
 * @param onGroupingChange Callback when the grouping preference changes
 * @param onSelectedValidationStampFilter Sets the new currently selected filter
 */
export default function ValidationStampFilterDropdown({
                                                          branch,
                                                          grouping,
                                                          onGroupingChange,
                                                      }) {

    const vsfContext = useContext(ValidationStampFilterContext)

    const onSelect = (filter) => {
        return () => {
            vsfContext.selectFilter(filter)
        }
    }

    const onDropFilter = () => {
        vsfContext.selectFilter(undefined)
    }

    // TODO Deprecated
    const [filters, setFilters] = useState([])

    const buildMenu = () => {
        const menu = []
        // Loading existing validation stamp filters
        vsfContext.filters.forEach(filter => {
            menu.push({
                key: filter.id,
                label: <SelectableMenuItem
                    text={filter.name}
                    value={filter.id === vsfContext.selectedFilter?.id}
                    onChange={onSelect(filter)}
                    extra={
                        <>
                            {
                                isAuthorized(filter, "validation_stamp_filter", "edit") &&
                                !vsfContext.inlineEdition &&
                                <FaCheckDouble
                                    className="ot-command"
                                    title="Edits this filter using the branch view"
                                    onClick={() => vsfContext.startInlineEdition(filter)}
                                />
                            }
                            {
                                isAuthorized(filter, "validation_stamp_filter", "edit") &&
                                vsfContext.inlineEdition &&
                                <FaEyeSlash
                                    className="ot-command"
                                    title="Stops the edition of this filter"
                                    onClick={vsfContext.stopInlineEdition}
                                />
                            }
                            {
                                isAuthorized(filter, "validation_stamp_filter", "edit") &&
                                !vsfContext.inlineEdition &&
                                <FaEdit
                                    className="ot-command"
                                    title="Edits this filter"
                                    onClick={() => vsfContext.editFilter(filter)}
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
                onClick: vsfContext.newFilter,
            })
        }
        // Drop filter
        if (vsfContext.selectedFilter) {
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
        if (vsfContext.filters) {
            buildMenu()
        }
    }, [vsfContext.filters, grouping, vsfContext.inlineEdition, vsfContext.selectedFilter]);

    return (
        <>
            <Dropdown
                menu={{items}}
                trigger={['click']}
                className="ot-action"
            >
                <Button>
                    <Space>
                        <FaFilter color={vsfContext.selectedFilter ? 'orange' : undefined}/>
                        <Typography.Text>Validation filter</Typography.Text>
                        <FaCaretDown/>
                    </Space>
                </Button>
            </Dropdown>
        </>
    )
}