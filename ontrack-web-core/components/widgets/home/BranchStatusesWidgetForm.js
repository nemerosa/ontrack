import {Button, Form, Input, Space} from "antd";
import {useContext} from "react";
import SelectMultiplePromotionLevelNames from "@components/promotionLevels/SelectMultiplePromotionLevelNames";
import SelectMultipleValidationStampNames from "@components/validationStamps/SelectMultipleValidationStampNames";
import {FaPlus, FaTrash} from "react-icons/fa";
import SelectProjectBranch from "@components/branches/SelectProjectBranch";
import SelectInterval from "@components/common/SelectInterval";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import SortableList, {SortableItem} from "react-easy-sort";
import {formFieldArraySwap} from "@components/form/formUtils";

export default function BranchStatusesWidgetForm({promotions, validations, refreshInterval, branches, title}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    const onSortEnd = (oldIndex, newIndex) => {
        formFieldArraySwap(widgetEditionForm, "branches", oldIndex, newIndex)
    }

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="title"
                    label="Title"
                    initialValue={title}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="promotions"
                    label="List of promotions to display"
                    initialValue={promotions}
                >
                    <SelectMultiplePromotionLevelNames/>
                </Form.Item>
                <Form.Item
                    name="validations"
                    label="List of validations to display"
                    initialValue={validations}
                >
                    <SelectMultipleValidationStampNames/>
                </Form.Item>
                <Form.List name="branches" initialValue={branches}>
                    {(fields, {add, remove}) => (
                        <>
                            <SortableList onSortEnd={onSortEnd}>
                                {fields.map(({key, name, ...restField}) => (
                                    <SortableItem key={key}>
                                        <Space
                                            key={key}
                                            style={{
                                                display: 'flex',
                                                marginBottom: 8,
                                                backgroundColor: '#fcfcfc',
                                                border: 'dashed 1px blue',
                                                borderRadius: 8,
                                                paddingTop: 16,
                                                paddingLeft: 16,
                                                paddingRight: 16,
                                            }}
                                            align="baseline"
                                        >
                                            <Form.Item
                                                {...restField}
                                                name={name}
                                                label={
                                                    <Space>
                                                        <FaTrash onClick={() => remove(name)}/>
                                                        Branch
                                                    </Space>
                                                }
                                                rules={[
                                                    {
                                                        required: true,
                                                        message: 'Branch is required',
                                                    },
                                                ]}
                                            >
                                                <SelectProjectBranch/>
                                            </Form.Item>
                                        </Space>
                                    </SortableItem>
                                ))}
                            </SortableList>
                            <Form.Item>
                                <Button type="dashed"
                                        onClick={() => add('')} block
                                        icon={<FaPlus/>}
                                >
                                    Add branch
                                </Button>
                            </Form.Item>
                        </>
                    )}
                </Form.List>
                <Form.Item
                    name="refreshInterval"
                    label="Refresh interval"
                    initialValue={refreshInterval}
                >
                    <SelectInterval/>
                </Form.Item>
            </Form>
        </>
    )
}