import {Button, Form, Space} from "antd";
import {FaBan, FaFilter} from "react-icons/fa";
import TableFormSection from "@components/common/table/TableFormSection";

export default function FilterForm({setFilterFormData, onFilterFormValuesChanged, filterForm, filterExtraButtons}) {

    const [filterFormInstance] = Form.useForm()

    const onFilterFormFinish = (values) => {
        setFilterFormData(values)
    }

    const onFilterFormClear = () => {
        filterFormInstance.resetFields()
        setFilterFormData({})
    }

    return (
        <>
            <TableFormSection>
                <Form
                    layout="inline"
                    onFinish={onFilterFormFinish}
                    onValuesChange={onFilterFormValuesChanged}
                    form={filterFormInstance}
                    style={{
                        rowGap: 16,
                        columnGap: 8,
                    }}
                >
                    {filterForm}
                    {/* Filter */}
                    <Button
                        type="primary"
                        htmlType="submit"
                    >
                        <Space>
                            <FaFilter/>
                            Filter
                        </Space>
                    </Button>
                    {/* Clear */}
                    <Button
                        type="link"
                        onClick={onFilterFormClear}
                    >
                        <Space>
                            <FaBan/>
                            Clear filter
                        </Space>
                    </Button>
                    {/* Extra buttons */}
                    {filterExtraButtons}
                </Form>
            </TableFormSection>
        </>
    )
}